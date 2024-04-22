package com.algaworks.algamoneyapi.resource;

import com.algaworks.algamoneyapi.dto.Attachements;
import com.algaworks.algamoneyapi.dto.TransactionStatisticsByCategory;
import com.algaworks.algamoneyapi.dto.TransactionStatisticsByDay;
import com.algaworks.algamoneyapi.event.ResourceCreatedEvent;
import com.algaworks.algamoneyapi.exception.InactiveCustomerException;
import com.algaworks.algamoneyapi.model.SortField;
import com.algaworks.algamoneyapi.model.Transaction;
import com.algaworks.algamoneyapi.repository.TransactionRepository;
import com.algaworks.algamoneyapi.repository.projection.TransactionReport;
import com.algaworks.algamoneyapi.service.TransactionService;
import com.algaworks.algamoneyapi.storage.S3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/transaction")
public class TransactionResource {

    @Autowired private TransactionRepository transactionRepository;
    @Autowired private TransactionService transactionService;
    @Autowired private ApplicationEventPublisher publisher;
    @Autowired private S3 s3;

    @PostMapping("/anexo")
    @PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO') and hasAuthority('SCOPE_write')")
    public Attachements uploadAnexo(@RequestParam MultipartFile anexo) throws IOException {
        String name = s3.temporarySave(anexo);
        return new Attachements(name, s3.configureUrl(name));
    }

    @GetMapping("/relatorios/por-pessoa")
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and hasAuthority('SCOPE_read')")
    public ResponseEntity<byte[]> relatorioPorPessoa(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate inicio,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fim) throws Exception {
        byte[] relatorio = transactionService.relatorioPorPessoa(inicio, fim);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                .body(relatorio);
    }

    @GetMapping("/estatisticas/por-dia")
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and hasAuthority('SCOPE_read')")
    public List<TransactionStatisticsByDay> porDia() {
        return this.transactionRepository.porDia(LocalDate.now());
    }

    @GetMapping("/estatisticas/por-categoria")
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and hasAuthority('SCOPE_read')")
    public List<TransactionStatisticsByCategory> porCategoria() {
        return this.transactionRepository.porCategoria(LocalDate.now());
    }

    /*    @GetMapping
    public Page<Transaction> transactionsFilter(TransactionFilter transactionFilter, Pageable pageable) {
        return transactionRepository.filter(transactionFilter, pageable);
    }*/

    @GetMapping(params = "resumo")
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and hasAuthority('SCOPE_read')")
    public Page<TransactionReport> resumir(LancamentoFilter lancamentoFilter, Pageable pageable) {
        return lancamentoRepository.resumir(lancamentoFilter, pageable);
    }

    @GetMapping("/{codigo}")
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and hasAuthority('SCOPE_read')")
    public ResponseEntity<Transaction> buscarPeloCodigo(@PathVariable String codigo) {
        Optional<Transaction> transaction = transactionRepository.findById(codigo);
        return transaction.isPresent() ? ResponseEntity.ok(transaction.get()) : ResponseEntity.notFound().build();
    }
    @GetMapping
    public Page<Transaction> findAllByPage(@RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "2") int sizePerPage,
                                              @RequestParam(defaultValue = "ID") SortField sortField,
                                              @RequestParam(defaultValue = "DESC") Sort.Direction sortDirection) {
        Pageable pageable = PageRequest.of(page, sizePerPage, sortDirection, sortField.getDataBaseFieldName());
        return transactionService.findAllByPage(pageable);
    }

    @GetMapping("/filter")
    public Page<Transaction> findAllByDescriptionLike(Pageable pageable, String description, LocalDate stardDate, LocalDate endDate) {
        return transactionService.findAllByDescriptionContainingIgnoreCaseOrDueDateBetween(pageable, description, stardDate, endDate);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<Transaction> findTransactionById(@PathVariable String transactionId) {
        Optional<Transaction> transaction = transactionRepository.findById(transactionId);
        return transaction.isPresent() ? ResponseEntity.ok(transaction.get()) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO') and hasAuthority('SCOPE_write')")
    public ResponseEntity<Transaction> criar(@Valid @RequestBody Transaction lancamento, HttpServletResponse response) {
        Transaction persistedTransaction = transactionService.save(lancamento);
        publisher.publishEvent(new ResourceCreatedEvent(this, response, persistedTransaction.getTransactionId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(persistedTransaction);
    }

/*    @ExceptionHandler({ PessoaInexistenteOuInativaException.class })
    public ResponseEntity<Object> handlePessoaInexistenteOuInativaException(PessoaInexistenteOuInativaException ex) {
        String mensagemUsuario = messageSource.getMessage("pessoa.inexistente-ou-inativa", null, LocaleContextHolder.getLocale());
        String mensagemDesenvolvedor = ex.toString();
        List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
        return ResponseEntity.badRequest().body(erros);
    }*/

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@Valid @RequestBody Transaction transaction, HttpServletResponse response) throws InactiveCustomerException {
        Transaction createdTransaction = transactionService.createTransaction(transaction);
        publisher.publishEvent(new ResourceCreatedEvent(this, response, createdTransaction.getTransactionId()));
        return ResponseEntity.ok(createdTransaction);
    }

    @DeleteMapping("/{transactionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ROLE_REMOVER_LANCAMENTO') and hasAuthority('SCOPE_write')")
    public void deleteTransaction(@PathVariable String transactionId) {
        transactionRepository.deleteById(transactionId);
    }

    @PutMapping("/{transactionId}")
    @PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO')")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable String transactionId, @RequestBody Transaction transaction) {
        Transaction updatedTransaction = transactionService.updateTransaction(transactionId, transaction);
        return ResponseEntity.ok(updatedTransaction);
    }

}
