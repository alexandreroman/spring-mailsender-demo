# Send mails using Spring Boot

This project shows how to send mails from a Spring Boot app.

Sending mails is actually easy:
```java
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
```

All you need is to add this dependency to your project:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

Then, you'll be able to use `JavaMailSender` to interact with your SMTP server
to send mails.

## How to use it?

Compile this project using a JDK 8:
```bash
$ ./mvnw clean package
```

You need to configure this app by
[setting mail properties](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-email.html).

You should end up with a file `application.yml` like this:
```yaml
spring:
  mail:
    host: smtp.mydomain.com
    port: 587
    username: foo
    password: bar
```

This app is also using configuration properties to set sender & recipient addresses:
```yaml
mail:
  sender: johndoe@mydomain.com
  recipient: johndoe@mydomain.com
```

You may also set these properties using environment variables:
```bash
$ export MAIL_SENDER=johndoe@mydomain.com
$ export MAIL_RECIPIENT=johndoe@mydomain.com
```

Start this application:
```java
$ java -jar target/spring-mailsender-demo.jar
```

If you hit endpoint http://localhost:8080, a mail will be sent to the recipient:
```java
$ curl localhost:8080
Email sent to johndoe@mydomain.com%
```

## Deploy this app to Cloud Foundry

Using Cloud Foundry, it is really easy to deploy and configure this app.

Let's start by setting configuration properties. This app fully supports Cloud Foundry, using
[Spring Cloud Connectors](https://cloud.spring.io/spring-cloud-connectors/spring-cloud-cloud-foundry-connector.html#_smtp)
to automatically create a `JavaMailSender` instance using an
[user-provided service](https://docs.cloudfoundry.org/devguide/services/user-provided.html):
```bash
$ cf cups smtp -t smtp -p '{ "host": "smtp.mydomain.com", "port": 587, "user": "foo", "password": "bar" }'
```

The key thing here is to add a tag `smtp` to your service.

You're now ready to deploy this app:
```bash
$ cf push --no-start
```

Before sending an email, you need to set sender & recipient addresses.
Let's set these properties using environment variables:
```bash
$ cf set-env spring-mailsender-demo MAIL_SENDER "johndoe@mydomain.com" 
$ cf set-env spring-mailsender-demo MAIL_RECIPIENT "johndoe@mydomain.com" 
```

Apply this configuration by reloading this app:
```bash
$ cf restage spring-mailsender-demo
```

A random route has been allocated to this app.
Use this URL to hit the app endpoint:
```bash
$ curl spring-mailsender-demo-XXX.mydomain.com
Email sent to johndoe@mydomain.com%
```

## Contribute

Contributions are always welcome!

Feel free to open issues & send PR.

## License

Copyright &copy; 2019 [Pivotal Software, Inc](https://pivotal.io).

This project is licensed under the [Apache Software License version 2.0](https://www.apache.org/licenses/LICENSE-2.0).
