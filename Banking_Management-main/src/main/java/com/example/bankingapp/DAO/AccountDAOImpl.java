package com.example.bankingapp.DAO;


import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import com.example.bankingapp.model.Accounts;
import com.example.bankingapp.model.JwtAuthResponse;
import com.example.bankingapp.model.Login;
import com.example.bankingapp.model.RefreshToken;
import com.example.bankingapp.model.RegisterUser;
import com.example.bankingapp.model.Role;
import com.example.bankingapp.model.Transactions;
import com.example.bankingapp.repository.AccountRepository;
import com.example.bankingapp.repository.LoginRepository;
import com.example.bankingapp.repository.RefreshTokenRepository;
import com.example.bankingapp.repository.RegisterUsersRepository;
import com.example.bankingapp.repository.TransactionRepository;
import com.example.bankingapp.security.JwtTokenProvider;
import com.example.bankingapp.service.AccountServiceException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;

import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import jakarta.transaction.Transactional;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;


@Repository
public class AccountDAOImpl implements AccountDAO {

	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@Autowired
	public AccountRepository Repo;
	
	@Autowired
	public TransactionRepository transactionRepo;
	
	@Autowired
	public RegisterUsersRepository registerUsersRepository;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;;
    
    @Autowired
    private RefreshTokenServiceImple refreshTokenService;
	 
    @Override
    public JwtAuthResponse login(Login login) throws AccountServiceException {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            login.getUsernameOrEmail(),
                            login.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            RegisterUser user = (RegisterUser) authentication.getPrincipal();

            // 1. Generate access token
            String accessToken = jwtTokenProvider.generateToken(authentication);

            // 2. Create refresh token
            RefreshToken refreshToken =
                    refreshTokenService.createRefreshToken(user);

            // 3. Build response (IMPORTANT)
            JwtAuthResponse response = new JwtAuthResponse();
            response.setAccessToken(accessToken);
            response.setRefreshToken(refreshToken.getToken());
            response.setTokenType("Bearer");

            return response;   // ✅ MUST RETURN

        } catch (Exception e) {
            e.printStackTrace(); // TEMP: for debugging
            throw new AccountServiceException("Invalid credentials");
        }
    }
	
	@Override
	public JwtAuthResponse refreshAccessToken(String refreshToken) {
		// Fetch RefreshToken From DB
		
		RefreshToken refreshTokenValue = refreshTokenRepository
				.findByToken(refreshToken)
				.orElseThrow(() -> 
					new RuntimeException("Invalid Refresh Token"));
		
		
		//Verify the expiry
		refreshTokenService.verifyExpiration(refreshTokenValue);
		
		//get user from refreshtoken
		RegisterUser user = refreshTokenValue.getUser();
		
		//Create authentication object
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken (
					user, null, user.getAuthorities()
				);
				
		//Generate new access Token
		String newAccessToken = jwtTokenProvider.generateToken(authentication);
		
		//prepare response
		JwtAuthResponse response = new JwtAuthResponse();
		response.setAccessToken(newAccessToken);
		response.setRefreshToken(refreshToken);
		return response;
	}

	@Override
	public boolean registerUser(RegisterUser registerUser) {
		
		if (registerUsersRepository.findByEmail(registerUser.getEmail()).size() != 0) {
            return false; 
        }
		
		registerUser.setRoles(Set.of(Role.ROLE_USER));
		registerUser.setPassword(passwordEncoder.encode(registerUser.getPassword()));
		if(registerUsersRepository.save(registerUser) != null) return true;
		return false;
	}

	@Override
	public Accounts addAccount(Accounts account) {
		System.out.println(account.getEmail());
		System.out.println(Repo.findByEmail(account.getEmail()).size());
		
		if(Repo.findByEmail(account.getEmail()).isEmpty()) {
			return Repo.save(account);
		}
		return null;
	}

	
	 @Override
	 @Transactional
	 public int updatedAccountDetails(long accountNumber, String accountHolderName, Date dateOfBirth, String email, String mobileNumber, String address) {
	      return Repo.updateAccountDetails(accountNumber, accountHolderName, dateOfBirth, email, mobileNumber, address);
	 }

	@Override
	public Accounts getAccountdetailsByAccountNumber(long accountNumber) {
		Optional<Accounts>getData = Repo.findByAccountNumber(accountNumber);
		if(getData.isPresent()) {
			return getData.get();	
		}
		return null;
	}

	
	
	@Transactional
	@Override
	public Accounts deposit_money(Long account_number, double deposit_balance) {
		
		Accounts account = getAccountdetailsByAccountNumber(account_number);
		validateAccountOwner(account);
	    if (account != null) {
	   
	        account.setBalance(account.getBalance() + deposit_balance);
	        
	        Repo.save(account);
	       
	        Transactions transaction = new Transactions(0, null, account, deposit_balance, "deposit", new Date());
	        transactionRepo.save(transaction); 

	        return account;
	    }
	    return null;
	}


	@Transactional
	@Override
	public Accounts withdraw_money(Long account_number, double withdraw_balance) {
		// TODO Auto-generated method stub
		Accounts account = getAccountdetailsByAccountNumber(account_number);
		validateAccountOwner(account);
		if (account != null) {
			   
	        account.setBalance(account.getBalance() - withdraw_balance);
	        
	        Repo.save(account);
	       
	        Transactions transaction = new Transactions(0, null, account, withdraw_balance, "withdraw", new Date());
	        transactionRepo.save(transaction); 

	        return account;
	    }
	    return null;
	}
	
	@Override
	public double totalBalance(long accountNumber) {
	    Optional<Accounts> account = Repo.findByAccountNumber(accountNumber);
	    if (account.isPresent()) {
	        return account.get().getBalance();
	    }
	    return 0;
	}


	@Transactional
	@Override
	public String transferMoney(long fromAccountNumber, long toAccountNumber, double amount) {
		// TODO Auto-generated method stub
		Optional<Accounts> fromAccount = Repo.findByAccountNumber(fromAccountNumber);
		Optional<Accounts> toAccount = Repo.findByAccountNumber(toAccountNumber);

		
		if(fromAccount.isPresent() && toAccount.isPresent() && amount != 0) {
			Accounts senderAccount = fromAccount.get();
			Accounts receiverAccount = toAccount.get();
			
			if(senderAccount.getBalance()< amount) {
				return "Insufficient Balance";
			}
			senderAccount.setBalance(senderAccount.getBalance()-amount);
			receiverAccount.setBalance(receiverAccount.getBalance()+amount);
			
			Repo.save(senderAccount);
			Repo.save(receiverAccount);
			
			Transactions transaction = new Transactions(0, senderAccount, receiverAccount, amount, "completed", new Date());
			transactionRepo.save(transaction);
			
			return "Transfer successful";

		
		}
	return "Account not found";
	}


	@Override
	public List<Object[]> getTransactionHistory(Long accountNumber) {
		return transactionRepo.findByAccountNumber(accountNumber);
	}


	@Transactional
	@Override
	public int deleteByAccountNumber(Long accountNumber) {
		
		List<Accounts> accountList = Repo.findAllByAccountNumber(accountNumber);

	    if (!accountList.isEmpty()) {
	        Repo.deleteAllByAccountNumber(accountNumber);
	        return accountList.size();
	    }
		return 0;
		
	}

	public byte[] generatePdf(List<Object[]> transactions) throws DocumentException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, byteArrayOutputStream);

        document.open();
        
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Paragraph title = new Paragraph("Transaction History", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        
        Paragraph spacer = new Paragraph(" ");
        spacer.setSpacingBefore(10);
        document.add(spacer);

        PdfPTable table = new PdfPTable(6); 
        addTableHeader(table);
        addRows(table, transactions);

        document.add(table);
        document.close();

        return byteArrayOutputStream.toByteArray();
    }

    private void addTableHeader(PdfPTable table) {
        Stream.of("Transaction ID", "Status", "To Account", "From Account", "Amount", "Timestamp")
              .forEach(columnTitle -> {
                  PdfPCell header = new PdfPCell();
                  header.setPhrase(new Phrase(columnTitle));
                  table.addCell(header);
              });
    }

    private void addRows(PdfPTable table, List<Object[]> transactions) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (Object[] transaction : transactions) {
            table.addCell(transaction[0] != null ? transaction[0].toString() : "N/A"); // Transaction ID
            table.addCell(transaction[1] != null ? transaction[1].toString() : "N/A"); // Status
            table.addCell(transaction[2] != null ? transaction[2].toString() : "N/A"); // To Account
            table.addCell(transaction[3] != null ? transaction[3].toString() : "N/A"); // From Account
            table.addCell(transaction[4] != null ? transaction[4].toString() : "N/A"); // Amount
            table.addCell(transaction[5] != null ? dateFormat.format(transaction[5]) : "N/A"); // Timestamp
        }
    }

    @Override
    public void savePdfLocally(byte[] pdfData, String filePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(pdfData);
        }
    }
    
    private void validateAccountOwner(Accounts account) {
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	String loggeduser = auth.getName();
    	
    	boolean isAdmin = auth.getAuthorities()
    			.stream()
    			.anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    	
    	//Admin can access any account
    	if(isAdmin) return;
    	
    	//USER can access only own account
    	if(!account.getEmail().equals(loggeduser)) {
    		throw new AccessDeniedException("Unauthorized account access");
    	}
    	
    }

    @Transactional
	@Override
	public void assignRole(String email, Role role) {
		// TODO Auto-generated method stub
    	RegisterUser user = registerUsersRepository.findFirstByEmail(email)
    			.orElseThrow(() -> new RuntimeException("User not found"));
    	
    	Set<Role> roles = user.getRoles();
    	if(roles == null) {
    		roles = new HashSet<>();
    	}
    	
    	roles.add(role);
    	
    	user.setRoles(roles);
    	registerUsersRepository.save(user);
	}

	@Override
	public List<Accounts> getAllAccountsUsersDetails() {
		// TODO Auto-generated method stub
		return Repo.findAll();
	}

	@Override
	public void logOutByUser(RegisterUser user) {
		refreshTokenRepository.deleteByUser(user);
		
	}

	
}
