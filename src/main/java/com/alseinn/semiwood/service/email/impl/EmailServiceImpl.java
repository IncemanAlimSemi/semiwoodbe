package com.alseinn.semiwood.service.email.impl;

import com.alseinn.semiwood.entity.order.Order;
import com.alseinn.semiwood.entity.order.OrderItem;
import com.alseinn.semiwood.entity.user.User;
import com.alseinn.semiwood.service.about.impl.AboutServiceImpl;
import com.alseinn.semiwood.service.email.EmailService;
import com.alseinn.semiwood.utils.ResponseUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final ResponseUtils responseUtils;

    private static final Logger LOG = Logger.getLogger(AboutServiceImpl.class.getName());
    private final String WELCOME_SEMIWOOD = "Welcome to Semiwood";

    @Override
    public void sendRegisterEmail(String email, String fullname) {
        CompletableFuture.runAsync(() -> {
            try {
                LOG.info(responseUtils.getMessage("email.send.started"));
                Path path = Paths.get("src/main/resources/templates/email/ContactUsTemplate.html");
                String htmlContent = Files.readString(path, StandardCharsets.UTF_8);
                htmlContent = htmlContent.replace("${link}", "http://localhost:3000/");
                htmlContent = htmlContent.replace("${fullname}", fullname);
                javaMailSender.send(createMimeMessageHelper(email, WELCOME_SEMIWOOD, htmlContent));
                LOG.info(responseUtils.getMessage("email.send.success"));
            } catch (Exception e) {
                LOG.info(responseUtils.getMessage("email.send.error"));
            }
        });
    }

    @Override
    public void sendPurchaseEmail(Order order) {
        CompletableFuture.runAsync(() -> {
            try {
                LOG.info(responseUtils.getMessage("email.send.started"));
                Path path = Paths.get("src/main/resources/templates/email/purchase/PurchaseTemplate.html");
                String htmlContent = Files.readString(path, StandardCharsets.UTF_8);
                htmlContent = htmlContent.replace("${orderNumber}", order.getId().toString());
                htmlContent = htmlContent.replace("${date}", order.getDate().toString());
                htmlContent = htmlContent.replace("${rows}", getRows(order.getOrderItems()));
                htmlContent = htmlContent.replace("${orderTotal}", order.getTotalPrice().toString());

                User user = order.getUser();
                String email = Objects.nonNull(user) ? user.getEmail() : order.getEmail();

                javaMailSender.send(createMimeMessageHelper(email, WELCOME_SEMIWOOD, htmlContent));
                LOG.info(responseUtils.getMessage("email.send.success"));
            } catch (Exception e) {
                LOG.info(responseUtils.getMessage("email.send.error"));
            }
        });
    }

    private String getRows(Set<OrderItem> orderItems) {
        try {
            StringBuilder rows = new StringBuilder();
            Path path = Paths.get("src/main/resources/templates/email/purchase/PurchaseItemRow.html");
            String row = Files.readString(path, StandardCharsets.UTF_8);

            orderItems.forEach(orderItem -> {
                String tempRow = row;
                tempRow = tempRow.replace("${itemLink}", "http://localhost:3000/p/" + orderItem.getProduct().getId());
                tempRow = tempRow.replace("${itemName}", orderItem.getProduct().getName());
                tempRow = tempRow.replace("${itemPrice}", orderItem.getTotalPrice().toString());
                tempRow = tempRow.replace("${itemQuantity}", String.valueOf(orderItem.getQuantity()));
                tempRow = tempRow.replace("${imgSrc}", "data:image/png;base64,");
                rows.append(tempRow);
            });

            return rows.toString();
        } catch (Exception e) {

        }
        return "";
    }

    private MimeMessage createMimeMessageHelper(String email, String subject, String html) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        helper.setTo(email);
        helper.setSubject(subject);

        helper.setText(html, true);

        return mimeMessage;
    }
}
