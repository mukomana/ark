package au.org.theark.study.model.dao;

import java.util.Collection;
import java.util.List;

import au.org.theark.core.exception.ArkSystemException;
import au.org.theark.core.exception.EntityNotFoundException;
import au.org.theark.core.exception.StatusNotAvailableException;
import au.org.theark.study.model.entity.GenderType;
import au.org.theark.study.model.entity.LinkSubjectStudy;
import au.org.theark.study.model.entity.Phone;
import au.org.theark.study.model.entity.PhoneType;
import au.org.theark.study.model.entity.Study;
import au.org.theark.study.model.entity.StudyComp;
import au.org.theark.study.model.entity.StudyStatus;
import au.org.theark.study.model.entity.SubjectStatus;
import au.org.theark.study.model.entity.TitleType;
import au.org.theark.study.model.entity.VitalStatus;
import au.org.theark.study.model.vo.SubjectVO;

public interface IStudyDao {

	public void create(Study study);
	
	public void create(StudyComp studyComponent) throws ArkSystemException;
	
	public void update(StudyComp studyComponent) throws ArkSystemException;
	
	public List<Study> getStudy(Study study);
	
	/**
	 * Interface to get a list of Study Status reference data from the backend.
	 * These study status' are no associated with a study as such but can be used for
	 * displaying a list of options for a particular study.
	 * @return
	 */
	public List<StudyStatus> getListOfStudyStatus();
	
	public Study getStudy(Long id);
	
	public void updateStudy(Study study);
	
	public StudyStatus getStudyStatus(String statusName) throws StatusNotAvailableException;
	
	public List<StudyComp> searchStudyComp(StudyComp studyCompCriteria);
	
	/**
	 * A look up that returns a list of All Phone Types. Mobile, Land etc
	 * In the event that there is a database/runtime error it is wrapped into a ArkSystemException and returned
	 * @return List<PhoneType>
	 */
	public List<PhoneType> getListOfPhoneType();
	
	public void create(Phone phone) throws ArkSystemException;
	
	public Collection<TitleType> getTitleType();
	
	public Collection<VitalStatus> getVitalStatus();
	
	public Collection<GenderType> getGenderType();
	
	public Collection<SubjectStatus> getSubjectStatus();
	
	public void createSubject(SubjectVO subjectVO);
	
	public void updateSubject(SubjectVO subjectVO);
	
	public Collection<SubjectVO> getSubject(SubjectVO subjectVO);
	
	public LinkSubjectStudy getLinkSubjectStudy(Long id) throws EntityNotFoundException;
	
	

}
