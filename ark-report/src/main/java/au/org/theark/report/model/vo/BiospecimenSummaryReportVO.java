package au.org.theark.report.model.vo;

import au.org.theark.core.model.lims.entity.BioSampletype;
import au.org.theark.core.model.study.entity.Study;

public class BiospecimenSummaryReportVO extends GenericReportViewVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Study study;
	
	private String subjectUID;
	
	private BioSampletype biospecimenType;

	public BiospecimenSummaryReportVO() {
	}

	public Study getStudy() {
		return study;
	}

	public void setStudy(Study study) {
		this.study = study;
	}

	public String getSubjectUID() {
		return subjectUID;
	}

	public void setSubjectUID(String subjectUID) {
		this.subjectUID = subjectUID;
	}

	public BioSampletype getBiospecimenType() {
		return biospecimenType;
	}

	public void setBiospecimenType(BioSampletype biospecimenType) {
		this.biospecimenType = biospecimenType;
	}	
}
