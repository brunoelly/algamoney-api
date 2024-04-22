package com.algaworks.algamoneyapi.service;

import com.algaworks.algamoneyapi.exception.InactiveCustomerException;
import com.algaworks.algamoneyapi.exception.ResourceNotFoundException;
import com.algaworks.algamoneyapi.mail.Mailer;
import com.algaworks.algamoneyapi.model.Customer;
import com.algaworks.algamoneyapi.model.Transaction;
import com.algaworks.algamoneyapi.repository.CategoryRepository;
import com.algaworks.algamoneyapi.repository.CustomerRepository;
import com.algaworks.algamoneyapi.repository.TransactionRepository;
import com.algaworks.algamoneyapi.storage.S3;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TransactionService {

    private static final String DESTINATARIOS = "ROLE_PESQUISAR_LANCAMENTO";
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    @Autowired private MongoOperations mongoOperations;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private Mailer mailer;
    @Autowired private S3 s3;

    @Scheduled(cron = "0 0 6 * * *")
    public void expiredTransactionsWarning() {
        if (logger.isDebugEnabled()) {
            logger.debug("Preparing to "
                    + "send warning emails expiredTransactions.");
        }

        List<Transaction> expiredTransactions = transactionRepository
                .findByDataVencimentoLessThanEqualAndDataPagamentoIsNull(LocalDate.now());

        if (expiredTransactions.isEmpty()) {
            logger.info("Sem lançamentos expiredTransactions para aviso.");
            return;
        }

        logger.info("Existem {} lançamentos expiredTransactions.", expiredTransactions.size());

        List<Customer> receiver = customerRepository
                .findBy(DESTINATARIOS);

        if (receiver.isEmpty()) {
            logger.warn("Existem lançamentos expiredTransactions, mas o "
                    + "sistema não encontrou destinatários.");
            return;
        }

        mailer.expiredTransactionsWarning(expiredTransactions, receiver);

        logger.info("Envio de e-mail de aviso concluído.");
    }


    public byte[] relatorioPorPessoa(LocalDate inicio, LocalDate fim) throws Exception {
        List<LancamentoEstatisticaPessoa> dados = lancamentoRepository.porPessoa(inicio, fim);

        Map<String, Object> parametros = new HashMap<>();
        parametr

    public Transaction createTransaction(Transaction transaction) throws InactiveCustomerException {
        Customer customer = customerRepository.findById(transaction.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id " + transaction.getCustomerId()));

        if (!customer.isActive()) {
            throw new InactiveCustomerException("Cannot create a transaction for an inactive customer with id " + customer.getCustomerId());
        }

        categoryRepository.findById(transaction.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + transaction.getCategoryId()));

        return transactionRepository.save(transaction);
    }

    public Transaction updateTransaction(String transactionId, Transaction updatedTransaction) {
        transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id " + transactionId));

        Document document = new Document();
        mongoOperations.getConverter().write(updatedTransaction, document);
        Update update = new Update();
        document.forEach(update::set);

        return mongoOperations.findAndModify(
                Query.query(Criteria.where("_id").is(transactionId)),
                update,
                Transaction.class);
    }

    public Page<Transaction> findAllByPage(Pageable pageable) {
        return transactionRepository.findAll(pageable)
                .map(transaction -> {
                    transaction.setDescription(transaction.getDescription());
                    transaction.setDueDate(transaction.getDueDate());
                    return transaction;
                });
    }

    public Page<Transaction> findAllByDescriptionContainingIgnoreCaseOrDueDateBetween(Pageable pageable, String description, LocalDate stardDate, LocalDate endDate) {
        return transactionRepository.findAllByDescriptionContainingIgnoreCaseOrDueDateBetween(pageable, description, stardDate, endDate);
    }
}
