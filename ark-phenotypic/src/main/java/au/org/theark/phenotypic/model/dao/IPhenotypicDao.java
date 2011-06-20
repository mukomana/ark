package au.org.theark.phenotypic.model.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import au.org.theark.core.exception.ArkSystemException;
import au.org.theark.core.exception.EntityCannotBeRemoved;
import au.org.theark.core.exception.EntityNotFoundException;
import au.org.theark.core.model.pheno.entity.DelimiterType;
import au.org.theark.core.model.pheno.entity.Field;
import au.org.theark.core.model.pheno.entity.FieldData;
import au.org.theark.core.model.pheno.entity.FieldDataLog;
import au.org.theark.core.model.pheno.entity.FieldPhenoCollection;
import au.org.theark.core.model.pheno.entity.FieldType;
import au.org.theark.core.model.pheno.entity.FileFormat;
import au.org.theark.core.model.pheno.entity.PhenoCollection;
import au.org.theark.core.model.pheno.entity.PhenoCollectionUpload;
import au.org.theark.core.model.pheno.entity.PhenoUpload;
import au.org.theark.core.model.pheno.entity.Status;
import au.org.theark.core.model.study.entity.LinkSubjectStudy;
import au.org.theark.core.model.study.entity.Study;
import au.org.theark.core.util.BarChartResult;
import au.org.theark.phenotypic.model.vo.PhenoCollectionVO;
import au.org.theark.phenotypic.model.vo.UploadVO;

/**
 * Interface for all select/insert/update/delete operations on the backend database.
 * 
 */
public interface IPhenotypicDao {

	// Collection
	public PhenoCollection getPhenotypicCollection(Long id);
	public java.util.Collection<PhenoCollection> getPhenotypicCollection();
	public java.util.Collection<PhenoCollection> getPhenotypicCollectionByStudy(Study study);
	public PhenoCollectionVO getPhenoCollectionAndFields(Long id);
	public java.util.Collection<PhenoCollection> searchPhenotypicCollection(PhenoCollection collectionToMatch);
	public PhenoCollection getPhenoCollectionByUpload(PhenoUpload upload);
	public void createPhenoCollection(PhenoCollection collection);
	public void createPhenoCollection(PhenoCollectionVO collectionVo);
	public void updatePhenoCollection(PhenoCollection collection);
	public void updatePhenoCollection(PhenoCollectionVO collectionVo);
	public void deletePhenoCollection(PhenoCollection collection) throws ArkSystemException, EntityCannotBeRemoved;
	public void deletePhenoCollection(PhenoCollectionVO collectionVo);
	public int clearPhenoCollection(PhenoCollection phenoCollection);
	
	// FieldCollection
	public java.util.Collection<FieldPhenoCollection> getFieldPhenoCollection(PhenoCollection phenoCollection);
	
	// Collection Upload
	public PhenoCollectionUpload getCollectionUpload(Long id);
	public java.util.Collection<PhenoCollectionUpload> getCollectionUpload();
	public PhenoCollectionVO getPhenoCollectionAndUploads(PhenoCollection phenoCollection);
	public java.util.Collection<PhenoCollectionUpload> searchCollectionUpload(PhenoCollectionUpload phenoCollectionUploadToMatch);
	public void createCollectionUpload(PhenoCollectionUpload phenoCollectionUpload);
	public void updateCollectionUpload(PhenoCollectionUpload phenoCollectionUpload);
	public void deleteCollectionUpload(PhenoCollectionUpload phenoCollectionUpload);
	
	// Status
	public Status getStatus(Long statusId);
	public Status getStatusByName(String statusName);
	public java.util.Collection<Status> getStatus();
	public void createStatus(Status status);
	public void updateStatus(Status status);
	
	// Field
	public Field getField(Long fieldId);
	public java.util.Collection<Field> getField();
	public Field getFieldByNameAndStudy(String fieldName, Study study) throws EntityNotFoundException;
	public java.util.Collection<Field> searchField(Field field);
	public boolean fieldHasData(Field field);
	public void createField(Field field);
	public void updateField(Field field);
	public void deleteField(Field field) throws ArkSystemException, EntityCannotBeRemoved;
	
	// Field Type
	public FieldType getFieldType(Long id);
	public FieldType getFieldTypeByName(String fieldTypeName);
	public java.util.Collection<FieldType> getFieldTypes();
	public void createFieldType(FieldType fieldType);
	public void updateFieldType(FieldType fieldType);
	
	// Field_collections
	public FieldPhenoCollection getFieldPhenoCollection(FieldPhenoCollection fieldPhenoCollection);
	public void createFieldPhenoCollection(FieldPhenoCollection fieldPhenoCollection);
	public void updateFieldPhenoCollection(FieldPhenoCollection fieldPhenoCollection);

	// Field Data
	public FieldData getFieldData(Long id);
	public FieldData getFieldData(FieldData fieldData);
	public java.util.Collection<FieldData> getFieldDataByCollectionAndField(PhenoCollection phenoCollection, Field field);
	public FieldData getFieldData(PhenoCollection phenoCollection, LinkSubjectStudy linkSubjectStudy, Field field, Date dateCollected, String value);
	public Collection<FieldData> searchFieldDataByField(Field field);
	public Collection<FieldData> searchFieldData(FieldData fieldData);
	public void createFieldData(FieldData fieldData);
	public void updateFieldData(FieldData fieldData);
	public void deleteFieldData(FieldData fieldData);
	
	// Field Data Log
	public FieldDataLog getFieldDataLog(Long id);
	public java.util.Collection<FieldDataLog> getFieldDataLogByField(Field field);
	public void createFieldDataLog(FieldDataLog fieldDataLog);
	
	// Upload
	public java.util.Collection<PhenoUpload> searchUpload(PhenoUpload upload);
	public java.util.Collection<PhenoUpload> searchUploadByCollection(PhenoCollection phenoCollection);
	public PhenoCollectionVO getPhenoCollectionAndUploads(Long id);
	public PhenoUpload getUpload(Long id);
	public void createUpload(PhenoUpload upload);
	public void createUpload(UploadVO uploadVo);
	public void updateUpload(PhenoUpload upload);
	public void deleteUpload(PhenoUpload upload) throws ArkSystemException, EntityCannotBeRemoved;
	
	// Collection Upload
	public PhenoCollectionUpload getPhenoCollectionUpload(Long id);
	public java.util.Collection<PhenoCollectionUpload> searchPhenoCollectionUpload(PhenoCollectionUpload phenoCollectionUpload);
	public void createPhenoCollectionUpload(PhenoCollectionUpload phenoCollectionUpload);
	public void updatePhenoCollectionUpload(PhenoCollectionUpload phenoCollectionUpload);
	public void deletePhenoCollectionUpload(PhenoCollectionUpload phenoCollectionUpload);
	
	// File Formats
	public java.util.Collection<FileFormat> getFileFormats();
	
	// Delimiter Types
	public java.util.Collection<DelimiterType> getDelimiterTypes();
	
	public int getCountOfFieldsInStudy(Study study);
	public int getCountOfFieldsWithDataInStudy(Study study);
	public Collection<FieldData> searchFieldDataBySubjectAndDateCollected(LinkSubjectStudy linkSubjectStudy, java.util.Date dateCollected);
	
	// Field Upload
	public Collection<PhenoUpload> searchFieldUpload(PhenoUpload phenoUpload);
	public int getCountOfCollectionsInStudy(Study study);
	public int getCountOfCollectionsWithDataInStudy(Study study);
	
	/**
	 * A generic interface that will return a list PhenoCollectionVO specified by a particular criteria, and a paginated reference point
	 * @return Collection of PhenoCollectionVO
	 */
	public List<PhenoCollectionVO> searchPageableFieldData(PhenoCollectionVO phenoCollectionVoCriteria, int first, int count);
	
	/**
	 * A generic interface that will return count of the fieldData's in the study
	 * @return int
	 */
	public int getStudyFieldDataCount(PhenoCollectionVO phenoCollectionVoCriteria);
	public DelimiterType getDelimiterType(Long id);
	
	public java.util.List<BarChartResult> getFieldsWithDataResults(Study study);
	public String getDelimiterTypeByDelimiterChar(char phenotypicDelimChr);
	public FileFormat getFileFormatByName(String name);
	
}
