package com.algaworks.algamoneyapi.mail;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.algaworks.algamoneyapi.model.ApplicationUser;
import com.algaworks.algamoneyapi.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class Mailer {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine thymeleaf;

//	@Autowired
//	private LancamentoRepository repo;
//
//	@EventListener
//	private void teste(ApplicationReadyEvent event) {
//		String template = "mail/aviso-lancamentos-vencidos";
//
//		List<Lancamento> lista = repo.findAll();
//
//		Map<String, Object> variaveis = new HashMap<>();
//		variaveis.put("lancamentos", lista);
//
//		this.enviarEmail("testes.algaworks@gmail.com",
//				Arrays.asList("alexandre.algaworks@gmail.com"),
//				"Testando", template, variaveis);
//		System.out.println("Terminado o envio de e-mail...");
//	}

    public void expiredTransactionsWarning(
            List<Transaction> expired, List<ApplicationUser> receiver) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("lancamentos", expired);

        List<String> emails = receiver.stream()
                .map(u -> u.getUserEmail())
                .collect(Collectors.toList());

        this.sendEmail("testes.algaworks@gmail.com",
                emails,
                "Expired transactions",
                "mail/warning-expired-transactions",
                variables);
    }

    public void sendEmail(String sender,
                          List<String> receiver, String subject, String template,
                          Map<String, Object> variables) {
        Context context = new Context(new Locale("pt", "BR"));

        variables.entrySet()
                .forEach(e -> context.setVariable(e.getKey(), e.getValue()));

        String message = thymeleaf.process(template, context);

        this.sendEmail(sender, receiver, subject, message);
    }

    public void sendEmail(String sender,
                          List<String> receiver, String subject, String message) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
            helper.setFrom(sender);
            helper.setTo(receiver.toArray(new String[receiver.size()]));
            helper.setSubject(subject);
            helper.setText(message, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Email sending has failed!", e);
        }
    }
}