package org.fastcode.templates.viewer;

import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.fastcode.FastCodeColorManager;
import org.fastcode.templates.rules.FastCodeTemplatePartitions;
import org.fastcode.templates.rules.SingleTokenScanner;

/**
 * Viewer configuration for fastcode templates.
 */
public class FastCodeTemplateViewerConfiguration extends SourceViewerConfiguration {

	private ITextHover				textHover;
	private ITokenScanner			tokenScanner;
	private IAutoEditStrategy[]		autoEditStrategies;
	private IContentAssistProcessor	assistProcessor;
	private IContentAssistProcessor	fcTagProcessor;

	/**
	 * Instantiates a new template viewer configuration.
	 *
	 * @param tokenScanner the token scanner
	 * @param textHover the text hover
	 */
	public FastCodeTemplateViewerConfiguration(final ITokenScanner tokenScanner, final ITextHover textHover) {
		this(tokenScanner, textHover, null, null, null);
	}

	/**
	 * Instantiates a new template viewer configuration.
	 *
	 * @param tokenScanner the token scanner
	 * @param textHover the text hover
	 * @param autoEditStrategies the auto edit strategies
	 * @param assistProcessor the assist processor
	 */
	public FastCodeTemplateViewerConfiguration(final ITokenScanner tokenScanner, final ITextHover textHover, final IAutoEditStrategy[] autoEditStrategies, final IContentAssistProcessor assistProcessor, final IContentAssistProcessor fcTagProcessor) {
		this.textHover = textHover;
		this.tokenScanner = tokenScanner;
		this.assistProcessor = assistProcessor;
		this.autoEditStrategies = autoEditStrategies;
		this.fcTagProcessor = fcTagProcessor;
	}

	public FastCodeTemplateViewerConfiguration() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getConfiguredContentTypes(org.eclipse.jface.text.source.ISourceViewer)
	 */
	@Override
	public String[] getConfiguredContentTypes(final ISourceViewer sourceViewer) {
		return new String[] { IDocument.DEFAULT_CONTENT_TYPE, FastCodeTemplatePartitions.SINGLE_LINE_COMMENT,
				FastCodeTemplatePartitions.MULTI_LINE_COMMENT,
				FastCodeTemplatePartitions.FC_METHOD,
				FastCodeTemplatePartitions.FC_FIELD,
				FastCodeTemplatePartitions.FC_CLASS,
				FastCodeTemplatePartitions.FC_FILE,
				FastCodeTemplatePartitions.FC_PACKAGE,
				FastCodeTemplatePartitions.FC_FOLDER,
				FastCodeTemplatePartitions.FC_PROJECT,
				FastCodeTemplatePartitions.FC_MESSAGE,
				FastCodeTemplatePartitions.FC_EXIT,
				FastCodeTemplatePartitions.FC_IMPORT,
				FastCodeTemplatePartitions.FC_XML,
				FastCodeTemplatePartitions.FC_CLASSES,
				FastCodeTemplatePartitions.FC_FILES,
				FastCodeTemplatePartitions.FC_PROPERTY,
				FastCodeTemplatePartitions.FC_INFO,
				FastCodeTemplatePartitions.FC_SNIPPET
				};
	}

	/*
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getAutoEditStrategies(org.eclipse.jface.text.source.ISourceViewer, java.lang.String)
	 */
	@Override
	public IAutoEditStrategy[] getAutoEditStrategies(final ISourceViewer sourceViewer, final String contentType) {
		return this.autoEditStrategies;
	}

	/*
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getContentAssistant(org.eclipse.jface.text.source.ISourceViewer)
	 */
	@Override
	public IContentAssistant getContentAssistant(final ISourceViewer sourceViewer) {
		if (this.assistProcessor == null) {
			return null;
		}
		final ContentAssistant assistant = new ContentAssistant();
		assistant.setDocumentPartitioning(FastCodeTemplatePartitions.TEMPLATE_PARTITIONING);
		//assistant.setDocumentPartitioning(FastCodeTemplatePartitions.FC_METHOD);
		assistant.setContentAssistProcessor(this.assistProcessor, IDocument.DEFAULT_CONTENT_TYPE);
		assistant.setContentAssistProcessor(this.fcTagProcessor, FastCodeTemplatePartitions.FC_METHOD);
		assistant.setContentAssistProcessor(this.fcTagProcessor, FastCodeTemplatePartitions.FC_FIELD);
		assistant.setContentAssistProcessor(this.fcTagProcessor, FastCodeTemplatePartitions.FC_CLASS);
		assistant.setContentAssistProcessor(this.fcTagProcessor, FastCodeTemplatePartitions.FC_FILE);
		assistant.setContentAssistProcessor(this.fcTagProcessor, FastCodeTemplatePartitions.FC_PACKAGE);
		assistant.setContentAssistProcessor(this.fcTagProcessor, FastCodeTemplatePartitions.FC_FOLDER);
		assistant.setContentAssistProcessor(this.fcTagProcessor, FastCodeTemplatePartitions.FC_PROJECT);
		assistant.setContentAssistProcessor(this.fcTagProcessor, FastCodeTemplatePartitions.FC_MESSAGE);
		assistant.setContentAssistProcessor(this.fcTagProcessor, FastCodeTemplatePartitions.FC_EXIT);
		assistant.setContentAssistProcessor(this.fcTagProcessor, FastCodeTemplatePartitions.FC_IMPORT);
		assistant.setContentAssistProcessor(this.fcTagProcessor, FastCodeTemplatePartitions.FC_XML);
		assistant.setContentAssistProcessor(this.fcTagProcessor, FastCodeTemplatePartitions.FC_CLASSES);
		assistant.setContentAssistProcessor(this.fcTagProcessor, FastCodeTemplatePartitions.FC_FILES);
		assistant.setContentAssistProcessor(this.fcTagProcessor, FastCodeTemplatePartitions.FC_PROPERTY);
		assistant.setContentAssistProcessor(this.fcTagProcessor, FastCodeTemplatePartitions.FC_INFO);
		assistant.setContentAssistProcessor(this.fcTagProcessor, FastCodeTemplatePartitions.FC_SNIPPET);

		//assistant.setEmptyMessage(message)
		assistant.enableAutoInsert(true);
		assistant.enableAutoActivation(true);
		assistant.setAutoActivationDelay(200);

		return assistant;
	}

	/*
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getTextHover(org.eclipse.jface.text.source.ISourceViewer, java.lang.String)
	 */
	@Override
	public ITextHover getTextHover(final ISourceViewer sourceViewer, final String contentType) {
		if (IDocument.DEFAULT_CONTENT_TYPE.equals(contentType)) {
			return this.textHover;
		}
		return null;
	}

	/*
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getPresentationReconciler(org.eclipse.jface.text.source.ISourceViewer)
	 */
	@Override
	public IPresentationReconciler getPresentationReconciler(final ISourceViewer sourceViewer) {
		final PresentationReconciler rec = new PresentationReconciler();
		rec.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));

		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(this.tokenScanner);
		rec.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		rec.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		dr = new DefaultDamagerRepairer(this.tokenScanner);
		rec.setDamager(dr, FastCodeTemplatePartitions.FC_METHOD);
		rec.setRepairer(dr, FastCodeTemplatePartitions.FC_METHOD);

		dr = new DefaultDamagerRepairer(this.tokenScanner);
		rec.setDamager(dr, FastCodeTemplatePartitions.FC_FIELD);
		rec.setRepairer(dr, FastCodeTemplatePartitions.FC_FIELD);

		dr = new DefaultDamagerRepairer(this.tokenScanner);
		rec.setDamager(dr, FastCodeTemplatePartitions.FC_CLASS);
		rec.setRepairer(dr, FastCodeTemplatePartitions.FC_CLASS);

		dr = new DefaultDamagerRepairer(this.tokenScanner);
		rec.setDamager(dr, FastCodeTemplatePartitions.FC_FILE);
		rec.setRepairer(dr, FastCodeTemplatePartitions.FC_FILE);

		dr = new DefaultDamagerRepairer(this.tokenScanner);
		rec.setDamager(dr, FastCodeTemplatePartitions.FC_PACKAGE);
		rec.setRepairer(dr, FastCodeTemplatePartitions.FC_PACKAGE);

		dr = new DefaultDamagerRepairer(this.tokenScanner);
		rec.setDamager(dr, FastCodeTemplatePartitions.FC_FOLDER);
		rec.setRepairer(dr, FastCodeTemplatePartitions.FC_FOLDER);

		dr = new DefaultDamagerRepairer(this.tokenScanner);
		rec.setDamager(dr, FastCodeTemplatePartitions.FC_PROJECT);
		rec.setRepairer(dr, FastCodeTemplatePartitions.FC_PROJECT);

		dr = new DefaultDamagerRepairer(this.tokenScanner);
		rec.setDamager(dr, FastCodeTemplatePartitions.FC_MESSAGE);
		rec.setRepairer(dr, FastCodeTemplatePartitions.FC_MESSAGE);

		dr = new DefaultDamagerRepairer(this.tokenScanner);
		rec.setDamager(dr, FastCodeTemplatePartitions.FC_EXIT);
		rec.setRepairer(dr, FastCodeTemplatePartitions.FC_EXIT);

		dr = new DefaultDamagerRepairer(this.tokenScanner);
		rec.setDamager(dr, FastCodeTemplatePartitions.FC_IMPORT);
		rec.setRepairer(dr, FastCodeTemplatePartitions.FC_IMPORT);

		dr = new DefaultDamagerRepairer(this.tokenScanner);
		rec.setDamager(dr, FastCodeTemplatePartitions.FC_XML);
		rec.setRepairer(dr, FastCodeTemplatePartitions.FC_XML);

		dr = new DefaultDamagerRepairer(this.tokenScanner);
		rec.setDamager(dr, FastCodeTemplatePartitions.FC_CLASSES);
		rec.setRepairer(dr, FastCodeTemplatePartitions.FC_CLASSES);

		dr = new DefaultDamagerRepairer(this.tokenScanner);
		rec.setDamager(dr, FastCodeTemplatePartitions.FC_FILES);
		rec.setRepairer(dr, FastCodeTemplatePartitions.FC_FILES);

		dr = new DefaultDamagerRepairer(this.tokenScanner);
		rec.setDamager(dr, FastCodeTemplatePartitions.FC_PROPERTY);
		rec.setRepairer(dr, FastCodeTemplatePartitions.FC_PROPERTY);

		dr = new DefaultDamagerRepairer(this.tokenScanner);
		rec.setDamager(dr, FastCodeTemplatePartitions.FC_INFO);
		rec.setRepairer(dr, FastCodeTemplatePartitions.FC_INFO);

		dr = new DefaultDamagerRepairer(this.tokenScanner);
		rec.setDamager(dr, FastCodeTemplatePartitions.FC_SNIPPET);
		rec.setRepairer(dr, FastCodeTemplatePartitions.FC_SNIPPET);

		final IToken commentToken = FastCodeColorManager.getToken("COMMENT");

		dr = new DefaultDamagerRepairer(new SingleTokenScanner(commentToken));
		rec.setDamager(dr, FastCodeTemplatePartitions.SINGLE_LINE_COMMENT);
		rec.setRepairer(dr, FastCodeTemplatePartitions.SINGLE_LINE_COMMENT);

		//		dr = new DefaultDamagerRepairer(new SingleTokenScanner(commentToken));
		//		rec.setDamager (dr, FastCodeTemplatePartitions.TEMPLATE_CODE);
		//		rec.setRepairer(dr, FastCodeTemplatePartitions.TEMPLATE_CODE);

		dr = new DefaultDamagerRepairer(new SingleTokenScanner(commentToken));
		rec.setDamager(dr, FastCodeTemplatePartitions.MULTI_LINE_COMMENT);
		rec.setRepairer(dr, FastCodeTemplatePartitions.MULTI_LINE_COMMENT);



		return rec;
	}

	/*
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getConfiguredDocumentPartitioning(org.eclipse.jface.text.source.ISourceViewer)
	 */
	@Override
	public String getConfiguredDocumentPartitioning(final ISourceViewer sourceViewer) {
		return FastCodeTemplatePartitions.TEMPLATE_PARTITIONING;
	}
}