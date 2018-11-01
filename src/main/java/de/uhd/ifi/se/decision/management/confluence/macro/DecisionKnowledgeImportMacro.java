package de.uhd.ifi.se.decision.management.confluence.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import de.uhd.ifi.se.decision.management.confluence.rest.JsonIssueKeeping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Map;


@Scanned
public class DecisionKnowledgeImportMacro implements Macro {

	private PageBuilderService pageBuilderService;

	@Autowired
	public DecisionKnowledgeImportMacro(@ComponentImport PageBuilderService pageBuilderService) {
		this.pageBuilderService = pageBuilderService;
	}

	public String execute(Map<String, String> map, String s, ConversionContext conversionContext) throws MacroExecutionException {
		pageBuilderService.assembler().resources().requireWebResource("de.uhd.ifi.se.decision.management.confluence.macro:issue-import-macro-web-resources");
		int pageId = Integer.parseInt(conversionContext.getEntity().getIdAsString());
		// Retrieve the instance of our JsonKeeping.
		JsonIssueKeeping jsonIssueKeeping = JsonIssueKeeping.getInstance();
		// Save all issues in an ArrayList data structure.
		ArrayList jsonIssueArray = jsonIssueKeeping.getJsonArrayFromPageId(pageId);
		// Create a new context for rendering...

		Map renderContext = MacroUtils.defaultVelocityContext();
		renderContext.put("jsonIssues", jsonIssueArray);

		return VelocityUtils.getRenderedTemplate("/templates/jsonTable.vm", renderContext);

	}


	public BodyType getBodyType() {
		return BodyType.NONE;
	}

	public OutputType getOutputType() {
		return OutputType.BLOCK;
	}
}