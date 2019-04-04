/*
 * Copyright (c) 2019 Pivotal Software, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.alexandreroman.demos.springmailsenderdemo;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

@Data
@Component
@ConfigurationProperties(prefix = "mail")
class MailProperties {
    private String sender = "johndoe@nowhere.com";
    private String recipient = "johndoe@nowhere.com";
}

@RestController
@Slf4j
@RequiredArgsConstructor
class MailController {
    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    @GetMapping("/")
    String sendMail() throws MessagingException {
        log.info("Sending email to {}", mailProperties.getRecipient());

        final MimeMessage msg = mailSender.createMimeMessage();
        msg.setFrom(mailProperties.getSender());
        msg.addRecipients(Message.RecipientType.TO, mailProperties.getRecipient());
        msg.setSubject("Hello world!");
        msg.setText("This is an email sent by a Spring Boot app.");
        mailSender.send(msg);

        log.info("Email successfully sent");

        return "Email sent to " + mailProperties.getRecipient();
    }
}
