package au.org.theark.web.rest.service;

import static au.org.theark.study.web.Constants.MADELINE_PEDIGREE_TEMPLATE;
import static au.org.theark.study.web.Constants.PEDIGREE_TEMPLATE_EXT;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.x5.template.Chunk;
import com.x5.template.Theme;

import au.org.theark.core.Constants;
import au.org.theark.core.exception.ArkSubjectInsertException;
import au.org.theark.core.exception.ArkUniqueException;
import au.org.theark.core.exception.EntityNotFoundException;
import au.org.theark.core.jni.ArkMadelineProxy;
import au.org.theark.core.model.study.entity.GenderType;
import au.org.theark.core.model.study.entity.LinkSubjectPedigree;
import au.org.theark.core.model.study.entity.LinkSubjectStudy;
import au.org.theark.core.model.study.entity.LinkSubjectTwin;
import au.org.theark.core.model.study.entity.Person;
import au.org.theark.core.model.study.entity.Relationship;
import au.org.theark.core.model.study.entity.Study;
import au.org.theark.core.model.study.entity.StudyPedigreeConfiguration;
import au.org.theark.core.model.study.entity.SubjectStatus;
import au.org.theark.core.model.study.entity.TwinType;
import au.org.theark.core.model.study.entity.VitalStatus;
import au.org.theark.core.service.IArkCommonService;
import au.org.theark.core.vo.SubjectVO;
import au.org.theark.study.model.capsule.RelativeCapsule;
import au.org.theark.study.model.vo.RelationshipVo;
import au.org.theark.study.service.IStudyService;
import au.org.theark.study.util.PedigreeUploadValidator;
import au.org.theark.web.rest.model.RelationShipRequest;
import au.org.theark.web.rest.model.SubjectRequest;
import au.org.theark.web.rest.model.TwinRequest;
import au.org.theark.web.rest.model.ValidationType;

@Service("pedigreeService")
@Transactional
public class PedigreeWebServiceRestImpl implements IPedigreeWebServiceRest {
	
	@Autowired
	private IStudyService iStudyService;
	
	@Autowired
	private  IArkCommonService		iArkCommonService;
	
	private static final String ACTIVE_SUBJECT_STATUS="Subject";
	
	@Override
	public Boolean createSubject(SubjectVO subjectVO) {
		try {
				iStudyService.createSubject(subjectVO);
				return true;
		} catch (ArkUniqueException | ArkSubjectInsertException e) {
			e.printStackTrace();
			return false;
			
		}
	}
	@Override
	public Boolean generatePedigree(SubjectVO subjectVO) {
	List<RelationshipVo>	relationshipVos=iStudyService.generateSubjectPedigreeRelativeList(subjectVO.getLinkSubjectStudy().getSubjectUID(), subjectVO.getLinkSubjectStudy().getStudy().getId());
	return (relationshipVos.size()>0);
	}
	@Override
	public void createRelationShip(SubjectVO subjectVO, SubjectVO relativeVO,Relationship relationship) {
		LinkSubjectPedigree linkSubjectPedigree =new LinkSubjectPedigree();
		linkSubjectPedigree.setSubject(subjectVO.getLinkSubjectStudy());
		linkSubjectPedigree.setRelative(relativeVO.getLinkSubjectStudy());
		linkSubjectPedigree.setRelationship(relationship);
		iStudyService.create(linkSubjectPedigree);
		
	}
	@Override
	public LinkSubjectStudy getLinkSubjectStudyBySubjectUidAndStudy(String subjectUid, Study study) {
			return iStudyService.getLinkSubjectStudyBySubjectUidAndStudy(subjectUid, study);
	}
	@Override
	public Study getStudyByID(Long id) {
		return iStudyService.getStudy(id);
	}
	@Override
	public Boolean createTwin(SubjectVO subjectVO, SubjectVO relativeVO, TwinType twinType) {
		Long studyId=subjectVO.getLinkSubjectStudy().getStudy().getId();
		String subjectUid=subjectVO.getLinkSubjectStudy().getSubjectUID();
		String relativeSubjectUid=relativeVO.getLinkSubjectStudy().getSubjectUID();
		RelationshipVo relationshipVo=new RelationshipVo();
			if(twinType.getName().equals("MZ")){
				relationshipVo.setMz("MZ");
			}else if(twinType.getName().equals("DZ")){
				relationshipVo.setDz("DZ");
			}
			relationshipVo.setIndividualId(relativeSubjectUid);
			iStudyService.processPedigreeTwinRelationship(relationshipVo,subjectUid,studyId);
			return true;
	}
	@Override
	public Boolean isCircularValidationSuccessful(SubjectVO subjectVO, SubjectVO relativeVO) {
		
				String subjectUID=subjectVO.getLinkSubjectStudy().getSubjectUID();
				String parentUID=relativeVO.getLinkSubjectStudy().getSubjectUID();
				Long sessionStudyId=subjectVO.getLinkSubjectStudy().getStudy().getId();
		
				List<RelationshipVo> relatives = iStudyService.generateSubjectPedigreeRelativeList(subjectUID,sessionStudyId);
				// Circular validation
				StringBuilder pedigree = new StringBuilder();
				ArrayList<String> dummyParents = new ArrayList<String>();
				boolean firstLine = true;

				List<RelationshipVo> existingRelatives = new ArrayList<RelationshipVo>();
				existingRelatives.addAll(relatives);

				RelationshipVo proband = new RelationshipVo();
				proband.setIndividualId(subjectUID);
				for (RelationshipVo relative : existingRelatives) {
					if ("Father".equalsIgnoreCase(relative.getRelationship())) {
						proband.setFatherId(relative.getIndividualId());
					}

					if ("Mother".equalsIgnoreCase(relative.getRelationship())) {
						proband.setMotherId(relative.getIndividualId());
					}
				}

				if (subjectVO.getLinkSubjectStudy().getPerson().getGenderType().getName().startsWith("M")) {
					proband.setFatherId(parentUID);
				}
				else if (subjectVO.getLinkSubjectStudy().getPerson().getGenderType().getName().startsWith("F")) {
					proband.setMotherId(parentUID);
				}
				existingRelatives.add(proband);

				List<RelationshipVo> newRelatives = iStudyService.generateSubjectPedigreeRelativeList(parentUID, sessionStudyId);

				RelationshipVo parent = new RelationshipVo();
				parent.setIndividualId(parentUID);
				for (RelationshipVo relative : newRelatives) {
					if ("Father".equalsIgnoreCase(relative.getRelationship())) {
						parent.setFatherId(relative.getIndividualId());
					}

					if ("Mother".equalsIgnoreCase(relative.getRelationship())) {
						parent.setMotherId(relative.getIndividualId());
					}
				}

				newRelatives.add(parent);

				for (RelationshipVo relative : newRelatives) {
					if (!existingRelatives.contains(relative)) {
						existingRelatives.add(relative);
					}
					else {
						for (RelationshipVo existingRelative : existingRelatives) {
							if (relative.getIndividualId().equals(existingRelative.getIndividualId())) {
								if (existingRelative.getFatherId() == null) {
									existingRelative.setFatherId(relative.getFatherId());
								}
								if (existingRelative.getMotherId() == null) {
									existingRelative.setMotherId(relative.getMotherId());
								}
							}
						}
					}
				}

				for (RelationshipVo relative : existingRelatives) {
					String dummyParent = "D-";
					String father = relative.getFatherId();
					String mother = relative.getMotherId();
					String individual = relative.getIndividualId();

					if (father != null) {
						dummyParent = dummyParent + father;
					}

					if (mother != null) {
						dummyParent = dummyParent + mother;
					}

					if (!"D-".equals(dummyParent) && !dummyParents.contains(dummyParent)) {
						dummyParents.add(dummyParent);
						if (father != null) {
							if (firstLine) {
								pedigree.append(father + " " + dummyParent);
								firstLine = false;
							}
							else {
								pedigree.append("\n" + father + " " + dummyParent);
							}
						}
						if (mother != null) {
							if (firstLine) {
								pedigree.append(mother + " " + dummyParent);
								firstLine = false;
							}
							else {
								pedigree.append("\n" + mother + " " + dummyParent);
							}
						}
						pedigree.append("\n" + dummyParent + " " + individual);
					}
					else if (!"D-".equals(dummyParent)) {
						if (firstLine) {
							pedigree.append(dummyParent + " " + individual);
							firstLine = false;
						}
						else {
							pedigree.append("\n" + dummyParent + " " + individual);
						}
					}
				}
				Set<String> circularUIDs = PedigreeUploadValidator.getCircularUIDs(pedigree);
				return !(circularUIDs.size() > 0);
	}
	@Override
	public String getPedigreeView(String subjectUid, Long studyId) {
		final StringBuffer sb = new StringBuffer();
		sb.setLength(0);

		String familyId = null;
		StringBuffer columnList = new StringBuffer("IndividualId");
		Study study = iStudyService.getStudy(studyId);
		StudyPedigreeConfiguration config = study.getPedigreeConfiguration();

		if (config != null && config.isDobAllowed()) {
			columnList.append(" DOB");
		}

		if (config != null && config.isAgeAllowed()) {
			columnList.append(" Age");
		}

		RelativeCapsule[] relatives = iStudyService.generateSubjectPedigreeImageList(subjectUid, studyId);

		if (relatives.length > 2) {
			StringWriter out = null;
			familyId = relatives[0].getFamilyId();

			try {
				Theme theme = new Theme();
				Chunk chunk = theme.makeChunk(MADELINE_PEDIGREE_TEMPLATE, PEDIGREE_TEMPLATE_EXT);
				chunk.set("relatives", relatives);
				out = new StringWriter();
				chunk.render(out);
			}
			catch (IOException io) {
				io.printStackTrace();
			}
			catch (Exception e) {
				e.printStackTrace();
			}

			ArkMadelineProxy madeline = new ArkMadelineProxy();
			String madelineOutput = null;

			try {
				madelineOutput = madeline.generatePedigree(out.toString(), columnList.toString());
			}
			catch (Error e) {
				e.printStackTrace();
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
			finally {
				try {
					out.flush();
					out.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}

			// Execute madeline by runtime
			// Madeline execute independent of the Ark program
			// Not recommend this option because this may causes unexpected errors. Ex: FileNotFound Exception
			// try{
			//
			// File file = new File("/tmp");
			// Runtime.getRuntime().exec("madeline2 /tmp/cs_009.data",null, file);
			//
			// }catch(IOException ioe){
			// ioe.printStackTrace();
			// }

			try {
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
				Document doc = docBuilder.parse(new InputSource(new StringReader(madelineOutput)));

				XPath xpath = XPathFactory.newInstance().newXPath();

				String xPathExpression = "//text";

				NodeList nodes = (NodeList) xpath.evaluate(xPathExpression, doc, XPathConstants.NODESET);

				for (int idx = 0; idx < nodes.getLength(); idx++) {
					String nodeText = nodes.item(idx).getTextContent();
					if (nodeText != null) {
						// Replace family id and dummy subject uids by blank text
						if (nodeText.startsWith("_F") || nodeText.startsWith("!")) {
							nodes.item(idx).setTextContent("");
						}

						// Replace half generated Indiv.. by UID
						if (nodeText.startsWith("Indiv")) {
							nodes.item(idx).setTextContent("UID");
						}

					}
				}

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new StringWriter());
				transformer.transform(source, result);
				sb.append(result.getWriter().toString());
			}
			catch (Exception e) {
				sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<svg width=\"640\" height=\"480\" xmlns=\"http://www.w3.org/2000/svg\">\r\n <!-- Created with SVG-edit - http://svg-edit.googlecode.com/ -->\r\n <g>\r\n  <title>Layer 1</title>\r\n  <text transform=\"rotate(-0.0100589, 317.008, 97.5)\" xml:space=\"preserve\" text-anchor=\"middle\" font-family=\"serif\" font-size=\"24\" id=\"svg_3\" y=\"106\" x=\"317\" stroke-width=\"0\" stroke=\"#000000\" fill=\"#000000\">Pedigree Error</text>\r\n </g>\r\n</svg>");
				e.printStackTrace();
			}

		}
		else {
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<svg width=\"640\" height=\"480\" xmlns=\"http://www.w3.org/2000/svg\">\r\n <!-- Created with SVG-edit - http://svg-edit.googlecode.com/ -->\r\n <g>\r\n  <title>Layer 1</title>\r\n  <text transform=\"rotate(-0.0100589, 317.008, 97.5)\" xml:space=\"preserve\" text-anchor=\"middle\" font-family=\"serif\" font-size=\"24\" id=\"svg_3\" y=\"106\" x=\"317\" stroke-width=\"0\" stroke=\"#000000\" fill=\"#000000\">No Pedigree History</text>\r\n </g>\r\n</svg>");
		}
		return sb.toString();

	}
	@Override
	public  SubjectVO mapSubjectRequestToBusinessSubjectVO(SubjectRequest subjectRequestInsert){
		SubjectVO subjectVO=new SubjectVO();
		LinkSubjectStudy linkSubjectStudy=new LinkSubjectStudy();
		
		linkSubjectStudy.setStudy(iArkCommonService.getStudy(subjectRequestInsert.getStudyId()));
		
		Person person=new Person();
		
		person.setFirstName(subjectRequestInsert.getFirstName());
		person.setLastName(subjectRequestInsert.getLastName());
		person.setDateOfBirth(subjectRequestInsert.getDateOfBirth());
		person.setGenderType(iArkCommonService.getGenderType(subjectRequestInsert.getGenderTypeName()));
		person.setVitalStatus(iArkCommonService.getVitalStatus(subjectRequestInsert.getVitalStatusName()));
		linkSubjectStudy.setPerson(person);
		
		linkSubjectStudy.setSubjectStatus(iArkCommonService.getSubjectStatus(subjectRequestInsert.getSubjectStatusName()));
		linkSubjectStudy.setSubjectUID(subjectRequestInsert.getSubjectUID());
		
		subjectVO.setLinkSubjectStudy(linkSubjectStudy);
		return subjectVO;
	}
	
	@Override
	public SubjectVO mapUpdateSubjectRequestToBusinessSubjectVO(SubjectRequest subjectRequestUpdate) {
		SubjectVO subjectVO=new SubjectVO();
		Study study=iArkCommonService.getStudy(subjectRequestUpdate.getStudyId());
		LinkSubjectStudy linkSubjectStudy=iStudyService.getLinkSubjectStudyBySubjectUidAndStudy(subjectRequestUpdate.getSubjectUID(),study);
		Person person=linkSubjectStudy.getPerson();
		person.setFirstName(subjectRequestUpdate.getFirstName());
		person.setLastName(subjectRequestUpdate.getLastName());
		person.setDateOfBirth(subjectRequestUpdate.getDateOfBirth());
		person.setGenderType(iArkCommonService.getGenderType(subjectRequestUpdate.getGenderTypeName()));
		person.setVitalStatus(iArkCommonService.getVitalStatus(subjectRequestUpdate.getVitalStatusName()));
		linkSubjectStudy.setPerson(person);
		linkSubjectStudy.setSubjectStatus(iArkCommonService.getSubjectStatus(subjectRequestUpdate.getSubjectStatusName()));
		//linkSubjectStudy.setSubjectUID(subjectRequestUpdate.getSubjectUID());
		
		subjectVO.setLinkSubjectStudy(linkSubjectStudy);
		return subjectVO;
		
	}
	
	
	@Override
	public ValidationType validateForSubjectUIDForStudy(Long studyId,String uid) {
		Study study;
		//Check for valid(not null) study id.
		if(studyId!=null ){
			study=iArkCommonService.getStudy(studyId);
		}else{
			return ValidationType.INVALID_STUDY_ID;
		}
		
		//Check for valid study(already created)
		if(study==null || study.getId()==null){
			return ValidationType.NOT_EXSISTING_STUDY;
		}
		
		//Check subject uid exists.
		LinkSubjectStudy linkSubjectStudy=iStudyService.getLinkSubjectStudyBySubjectUidAndStudy(uid,study);
		if(linkSubjectStudy==null || linkSubjectStudy.getId()==null){
			return ValidationType.SUBJECT_UID_NOT_EXISTS;
		}
		return ValidationType.SUCCESSFULLY_VALIDATED;
	}
	@Override
	public ValidationType validateForStudy(Long studyId) {
		Study study;
		//Check for valid(not null) study id.
		if(studyId!=null ){
			study=iArkCommonService.getStudy(studyId);
		}else{
			return ValidationType.INVALID_STUDY_ID;
		}
		//Check for valid study(already created)
		if(study==null || study.getId()==null){
			return ValidationType.NOT_EXSISTING_STUDY;
		}
		return ValidationType.SUCCESSFULLY_VALIDATED;
	}
	@Override
	public ValidationType validateEntireSubjectRequest(SubjectRequest subjectRequest,String action){
		Study study;
		GenderType genderType;
		VitalStatus vitalStatus;
		SubjectStatus subjectStatus;
		//Check for valid(not null) study id.
		if(subjectRequest.getStudyId()!=null ){
			study=iArkCommonService.getStudy(subjectRequest.getStudyId());
		}else{
			return ValidationType.INVALID_STUDY_ID;
		}
		
		//Check for valid study(already created)
		if(study==null ||study.getId()==null){
			return ValidationType.NOT_EXSISTING_STUDY;
		}
		
		if(action.equals(Constants.ACTION_INSERT)){
			//Check for unique subject uid for the study.
			if(!iStudyService.isSubjectUIDUnique(study,subjectRequest.getSubjectUID(),action)){
				return ValidationType.SUBJECT_UID_ALREADY_EXISTS; 
			}
		}else if(action.equals(Constants.ACTION_UPDATE)){
			LinkSubjectStudy linkSubjectStudy=iStudyService.getLinkSubjectStudyBySubjectUidAndStudy(subjectRequest.getSubjectUID(), study);
			if(linkSubjectStudy==null ||linkSubjectStudy.getId()==null){
				return ValidationType.SUBJECT_UID_NOT_EXISTS;
			}else if(!linkSubjectStudy.getId().equals(subjectRequest.getId())){
				return ValidationType.MISMATCH_SUBJECT_UID_WITH_SUBJECT_ID;
			}
		}
		//Check for the valid gender type.
		if(subjectRequest.getGenderTypeName()!=null){
			genderType=iArkCommonService.getGenderType(subjectRequest.getGenderTypeName());
		}else{
			return ValidationType.NO_GENDERTYPE;
		}
		if(genderType==null || genderType.getId()==null){
			return ValidationType.INVALID_GENDER_TYPE;
		}
		
		//Check for the valid vital status.
		if(subjectRequest.getVitalStatusName()!=null){
			vitalStatus=iArkCommonService.getVitalStatus(subjectRequest.getVitalStatusName());
		}else{
			return ValidationType.NO_VITALTYPE;
		}
		if(vitalStatus==null || vitalStatus.getId()==null){
			return ValidationType.INVALID_VITAL_TYPE;
		}
		
		//Check for the valid subject Status
		if(subjectRequest.getSubjectStatusName()!=null){
			subjectStatus=iArkCommonService.getSubjectStatus(subjectRequest.getSubjectStatusName());
		}else{
			return ValidationType.NO_SUBJECT_STATUS;
		}
		if(subjectStatus==null || subjectStatus.getId()==null){
			return ValidationType.INVALID_SUBJECT_STATUS;
		}
		
		return ValidationType.SUCCESSFULLY_VALIDATED;
		
	}
	@Override
	public ValidationType validateRelationShipForStudy(RelationShipRequest createRelationShipRequest) {
		Study study;
		Relationship relationship;
		//Check for valid(not null) study id.
		if(createRelationShipRequest.getStudyId()!=null ){
			study=iArkCommonService.getStudy(createRelationShipRequest.getStudyId());
		}else{
			return ValidationType.INVALID_STUDY_ID;
		}
		//Check for valid study(already created)
		if(study==null || study.getId()==null){
			return ValidationType.NOT_EXSISTING_STUDY;
		}
		
		//Check subject uid exists.
		LinkSubjectStudy subject=iStudyService.getLinkSubjectStudyBySubjectUidAndStudy(createRelationShipRequest.getSubjectUID(),study);
		if(subject==null || subject.getId()==null){
			return ValidationType.SUBJECT_UID_NOT_EXISTS;
		}else if(!subject.getSubjectStatus().getName().equalsIgnoreCase(ACTIVE_SUBJECT_STATUS)){
			return ValidationType.SUBJECT_STATUS_NOT_ALLOWED_TO_CREATE_RELATIONSHIP;
		}
		LinkSubjectStudy relative=iStudyService.getLinkSubjectStudyBySubjectUidAndStudy(createRelationShipRequest.getRelativeUID(),study);
		if(relative==null || relative.getId()==null){
			return ValidationType.RELATIVE_SUBJECT_UID_NOT_EXISTS;
		}else if (!relative.getSubjectStatus().getName().equalsIgnoreCase(ACTIVE_SUBJECT_STATUS)){
			return ValidationType.RELATION_STATUS_NOT_ALLOWED_TO_CREATE_RELATIONSHIP;
		}
		if(createRelationShipRequest.getParentType()!=null){
			relationship=iArkCommonService.getRelationShipByname(createRelationShipRequest.getParentType());
				if(relationship==null || relationship.getId()==null){
					return ValidationType.INVALID_PARENT_TYPE;
				}
		}else{
				return ValidationType.NO_PARENT_TYPE;
		}
		
		LinkSubjectPedigree linkSubjectPedigree=iStudyService.getParentRelationShipByLinkSubjectStudies(subject, relative);
		
		if(linkSubjectPedigree!=null && linkSubjectPedigree.getId()!=null){
			return ValidationType.PARENT_RELATION_SHIP_ALREADY_EXISTS;
		}
		return ValidationType.SUCCESSFULLY_VALIDATED;
	}
	@Override
	public ValidationType validateTwinTypeForStudy(TwinRequest createTwinRequest) {
		Study study;
		TwinType twinType;
		//Check for valid(not null) study id.
		if(createTwinRequest.getStudyId()!=null ){
			study=iArkCommonService.getStudy(createTwinRequest.getStudyId());
		}else{
			return ValidationType.INVALID_STUDY_ID;
		}
		//Check for valid study(already created)
		if(study==null || study.getId()==null){
			return ValidationType.NOT_EXSISTING_STUDY;
		}
		
		//Check subject uid exists.
		LinkSubjectStudy subject=iStudyService.getLinkSubjectStudyBySubjectUidAndStudy(createTwinRequest.getSubjectUID(),study);
		if(subject==null || subject.getId()==null){
			return ValidationType.SUBJECT_UID_NOT_EXISTS;
		}else if(!subject.getSubjectStatus().getName().equalsIgnoreCase(ACTIVE_SUBJECT_STATUS)){
			return ValidationType.SUBJECT_STATUS_NOT_ALLOWED_TO_CREATE_RELATIONSHIP;
		}
		LinkSubjectStudy relative=iStudyService.getLinkSubjectStudyBySubjectUidAndStudy(createTwinRequest.getRelativeUID(),study);
		if(relative==null || relative.getId()==null){
			return ValidationType.RELATIVE_SUBJECT_UID_NOT_EXISTS;
		}else if (!relative.getSubjectStatus().getName().equalsIgnoreCase(ACTIVE_SUBJECT_STATUS)){
			return ValidationType.RELATION_STATUS_NOT_ALLOWED_TO_CREATE_RELATIONSHIP;
		}
		if(createTwinRequest.getTwinType()!=null){
			twinType=iArkCommonService.getTwinTypeByname(createTwinRequest.getTwinType());
				if(twinType==null || twinType.getId()==null){
					return ValidationType.INVALID_TWIN_TYPE;
				}
		}else{
			return ValidationType.NO_TWIN_TYPE;
		}
		
		LinkSubjectTwin linkSubjectTwin=iStudyService.getTwinRelationShipByLinkSubjectStudies(subject, relative);
		
		if(linkSubjectTwin!=null && linkSubjectTwin.getId()!=null){
			return ValidationType.TWIN_RELATION_SHIP_ALREADY_EXISTS;
		}
		return ValidationType.SUCCESSFULLY_VALIDATED;
	}
	@Override
	public ValidationType validateParentRelationShip(Long id) {
		LinkSubjectPedigree linkSubjectPedigree=iStudyService.getLinkSubjectPedigreeById(id);
		if(linkSubjectPedigree==null || linkSubjectPedigree.getId()==null){
			return ValidationType.INVALID_PARENT_RELATION_SHIP;
		}else{
			return ValidationType.SUCCESSFULLY_VALIDATED;
		}
		
	}
	@Override
	public ValidationType validateTwinRelationShip(Long id) {
		LinkSubjectTwin linkSubjectTwin=iStudyService.getLinkSubjectTwinById(id);
		if(linkSubjectTwin==null || linkSubjectTwin.getId()==null){
			return ValidationType.INVALID_TWIN_RELATION_SHIP;
		}else{
			return ValidationType.SUCCESSFULLY_VALIDATED;
		}
	}
	
	
	
	@Override
	public Relationship getRelationShipByname(String name) {
		return iArkCommonService.getRelationShipByname(name);
	}
	@Override
	public TwinType getTwinTypeByname(String name) {
		return iArkCommonService.getTwinTypeByname(name);
	}
	@Override
	public List<LinkSubjectStudy> getListofLinkSubjectStudiesForStudy(Study study) {
		return iArkCommonService.getListofLinkSubjectStudiesForStudy(study);
	}
	@Override
	public List<SubjectRequest> mapListOfLinkSubjectStudiesToListOfSubjectRequests(List<LinkSubjectStudy> linkSubjectStudies) {
		List<SubjectRequest> createSubjectRequests=new ArrayList<SubjectRequest>();
		for (LinkSubjectStudy linkSubjectStudy : linkSubjectStudies) {
			createSubjectRequests.add(mapLinkSubjectStudyToCreateSubjectRequests(linkSubjectStudy));
		}
		return createSubjectRequests;
	}
	@Override
	public SubjectRequest mapLinkSubjectStudyToCreateSubjectRequests(LinkSubjectStudy linkSubjectStudy) {
		SubjectRequest subjectRequest=new SubjectRequest();
		subjectRequest.setStudyId(linkSubjectStudy.getStudy().getId());
		subjectRequest.setId(linkSubjectStudy.getId());
		subjectRequest.setSubjectUID(linkSubjectStudy.getSubjectUID());
		subjectRequest.setFirstName(linkSubjectStudy.getPerson().getFirstName());
		subjectRequest.setLastName(linkSubjectStudy.getPerson().getLastName());
		subjectRequest.setDateOfBirth(linkSubjectStudy.getPerson().getDateOfBirth());
		subjectRequest.setGenderTypeName(linkSubjectStudy.getPerson().getGenderType().getName());
		subjectRequest.setVitalStatusName(linkSubjectStudy.getPerson().getVitalStatus().getName());
		subjectRequest.setSubjectStatusName(linkSubjectStudy.getSubjectStatus().getName());
		return subjectRequest;		
	}
	
	@Override
	public Boolean isRelativeASibling(SubjectVO subjectVO, SubjectVO relativeVO) {
		boolean isSibling=false;
		Long   studyId=subjectVO.getLinkSubjectStudy().getStudy().getId();
		String subjectUid=subjectVO.getLinkSubjectStudy().getSubjectUID();
		String relativeUid=relativeVO.getLinkSubjectStudy().getSubjectUID();
		List<RelationshipVo> mySibling= iStudyService.getSubjectPedigreeTwinList(subjectUid, studyId);
		for (RelationshipVo relationshipVo : mySibling) {
			if(relativeUid.equals(relationshipVo.getIndividualId())){
				isSibling=true;
				break;
			}
		}
		return isSibling;
	}
	@Override
	public Boolean updateSubject(SubjectVO subjectVO) {
		try {
			iStudyService.updateSubject(subjectVO);
			return true;
		} catch (ArkUniqueException | EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	@Override
	public void deleteRelationShip(Long id) {
		LinkSubjectPedigree linkSubjectPedigree=iStudyService.getLinkSubjectPedigreeById(id);
		iStudyService.deleteRelationship(linkSubjectPedigree);
	}
	@Override
	public void deleteTwin(Long id) {
		LinkSubjectTwin linkSubjectTwin=iStudyService.getLinkSubjectTwinById(id);
		iStudyService.delete(linkSubjectTwin);
	}
	@Override
	public List<LinkSubjectPedigree> getListofLinkSubjectPedigreeForStudy(Study study) {
		return iStudyService.getListOfLinkSubjectPedigreeForStudy(study);
	}
	
	@Override
	public List<LinkSubjectTwin> getListofLinkSubjectTwinForStudy(Study study) {
		return iStudyService.getListOfLinkSubjectTwinForStudy(study);
		
	}
	
	@Override
	public List<RelationShipRequest> mapListOfLinkSubjectPedigreesToListOfRelationShipRequests(List<LinkSubjectPedigree> linkSubjectPedigrees) {
		List<RelationShipRequest> relationShipRequests=new ArrayList<RelationShipRequest>();
			for (LinkSubjectPedigree linkSubjectPedigree : linkSubjectPedigrees) {
				RelationShipRequest relationShipRequest=new RelationShipRequest();
				relationShipRequest.setId(linkSubjectPedigree.getId().longValue());
				relationShipRequest.setStudyId(linkSubjectPedigree.getSubject().getStudy().getId());
				relationShipRequest.setSubjectUID(linkSubjectPedigree.getSubject().getSubjectUID());
				relationShipRequest.setRelativeUID(linkSubjectPedigree.getRelative().getSubjectUID());
				relationShipRequest.setParentType(linkSubjectPedigree.getRelationship().getName());
				relationShipRequests.add(relationShipRequest);
			}
		return relationShipRequests;
	}
	@Override
	public List<TwinRequest> mapListOfLinkSubjectTwinsToListOfTwinRequests(List<LinkSubjectTwin> linkSubjectTwins) {
		List<TwinRequest> twinRequests=new ArrayList<TwinRequest>();
		for (LinkSubjectTwin linkSubjectTwin : linkSubjectTwins) {
			TwinRequest twinRequest=new TwinRequest();
			twinRequest.setId(linkSubjectTwin.getId().longValue());
			twinRequest.setStudyId(linkSubjectTwin.getFirstSubject().getStudy().getId());
			twinRequest.setSubjectUID(linkSubjectTwin.getFirstSubject().getSubjectUID());
			twinRequest.setRelativeUID(linkSubjectTwin.getSecondSubject().getSubjectUID());
			twinRequest.setTwinType(linkSubjectTwin.getTwinType().getName());
			twinRequests.add(twinRequest);
		}
	return twinRequests;
		
		
	}
	
	
	
	
	
	
	

}
