package org.bonitasoft.esb.bean;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.bonitasoft.engine.bpm.contract.FileInputValue;

public class WordDocumentExtractorBean {

    private final static Log log = LogFactory.getLog(WordDocumentExtractorBean.class);

	@Handler
	public void processClaimsLetter(Exchange exchange) {
		
		Map<String, Serializable> result = new HashMap<String, Serializable>();

		try {
			
			String filename = (String)exchange.getIn().getHeader("CamelFileName");
			byte[] fileBytes = (byte[])exchange.getIn().getBody();
			ByteArrayInputStream bis = new ByteArrayInputStream(fileBytes);

			XWPFDocument docx = new XWPFDocument(bis);
//			// using XWPFWordExtractor Class
			XWPFWordExtractor we = new XWPFWordExtractor(docx);
			System.out.println(we.getText());
//			
			Pattern pattern = Pattern.compile("Customer reference: ([^\n]*)");
//		
			Matcher matcher = pattern.matcher(we.getText());
			matcher.find();
			String customerReference = matcher.group(1);
			System.out.println(customerReference);

			FileInputValue fileInput = new FileInputValue(filename, fileBytes);
			
			HashMap<String, Serializable> claim = new HashMap<String, Serializable>();
			claim.put("number", "1");
			//claim.put("needReview", true);
			//claim.put("customerId", customerReference);
			
			result.put("letter", fileInput);
			result.put("claimInput", claim);
			result.put("customerReference", customerReference);
			exchange.getOut().setBody(result);
			
			
		} catch (Exception e) {
			log.error("Unable to import Word document", e);
			throw new RuntimeException("Unable to import Word document", e);
		}
		//return result;
	}

}
