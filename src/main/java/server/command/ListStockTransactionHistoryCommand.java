package server.command;

import dao.CompanyService;
import dao.TransactionService;
import models.Company;
import models.Transaction;
import models.User;

import java.sql.SQLException;
import java.util.List;

public class ListStockTransactionHistoryCommand implements Command {

    private CompanyService companyService = CompanyService.getInstance();
    private TransactionService transactionService = TransactionService.getInstance();

    @Override
    public String runCommand(User currentUser, String[] args) {
        Company company = companyService.findByCode(args[1]);
        List<Transaction> transactions;

        if(company == null) {
            return "Company_code is not valid\n";
        }

        try {
            transactions = transactionService.getTransactionsByCompanyId(company.getId());
        } catch (SQLException ex) {
            return "Error in listing transaction history : " + ex.getMessage() + "\n";
        }

        StringBuilder result = new StringBuilder();

        result.append("TRANSACTIONS FOR COMPANY " + company.getName() + "\n");

        for(Transaction transaction: transactions) {
            result.append(
                    "Buyer id: " + transaction.getBuyerId() +
                    ", Seller id: " + transaction.getSellerId() +
                    ", Company id: " + transaction.getCompanyId() +
                    ", Number of units: " + transaction.getNumberOfUnits() +
                    ", Price per unit: " + transaction.getPricePerUnit() + "\n"
                    );
        }

        return result.toString();
    }
}
