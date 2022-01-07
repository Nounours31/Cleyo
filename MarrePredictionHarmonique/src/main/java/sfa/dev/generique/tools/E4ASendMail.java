package sfa.dev.generique.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class E4ASendMail {
	private final static String usermail="code.fages@gmail.com";
	private final static String username="code.fages@gmail.com";
	private final static String password="-"; // mot de passe applicatif
	
	private final static HashMap<String, String> _Properties = InitProp();
	private E4ALogger _logger = null;
	private final static javax.mail.Authenticator _authen = new javax.mail.Authenticator() {
		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(username, password);
		}
	};

	private static HashMap<String, String> InitProp () {
		HashMap<String, String> retour = new HashMap<String, String>(5);
		retour.put("mail.smtp.host", "smtp.gmail.com");
		retour.put("mail.smtp.port", "587");
		retour.put("mail.smtp.auth", "true");
		retour.put("mail.smtp.starttls.enable", "true"); //TLS
		return retour;
	}
	
	public E4ASendMail() {
		_logger = E4ALogger.getLogger(E4ASendMail.class.getCanonicalName());
	}

	public int SendEmailTLS (ArrayList<String> emails, String subject, String msg, ArrayList<String> attachmentFiles) {
		Properties prop = new Properties();
		Iterator<String> ite = _Properties.keySet().iterator();
		while (ite.hasNext()) {
			String key = ite.next();
			prop.put(key, _Properties.get(key));
		}
		

		Session session = Session.getInstance(prop,_authen);

		try {
			StringBuffer sb =  new StringBuffer();
			Iterator<String> ite2 = emails.iterator();
			boolean isNotFirst = false;
			while (ite2.hasNext()) {
				if (isNotFirst)
					sb.append(", ");
				sb.append(ite2.next());
				isNotFirst=true;
			}
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(usermail));
			message.setRecipients( Message.RecipientType.TO, InternetAddress.parse(sb.toString()));
				
			message.setSubject(subject);
			message.setText(msg);
			if (attachmentFiles != null) {
				for (String attachmentFile : attachmentFiles) {
					message.setFileName(attachmentFile);				
				}
			}
			Transport.send(message);
			_logger.debug ("Message envoye ...");
		} catch (MessagingException e) {
			e.printStackTrace();
			return 1;
		}
		return 0;
	}

}


