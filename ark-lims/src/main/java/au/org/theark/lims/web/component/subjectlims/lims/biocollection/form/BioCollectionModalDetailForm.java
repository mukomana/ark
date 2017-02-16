/*******************************************************************************
 * Copyright (c) 2011  University of Western Australia. All rights reserved.
 * 
 * This file is part of The Ark.
 * 
 * The Ark is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * 
 * The Ark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package au.org.theark.lims.web.component.subjectlims.lims.biocollection.form;

import java.util.Collection;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.org.theark.core.exception.ArkSystemException;
import au.org.theark.core.exception.EntityNotFoundException;
import au.org.theark.core.model.lims.entity.BioCollection;
import au.org.theark.core.model.study.entity.ArkFunction;
import au.org.theark.core.model.study.entity.CustomFieldCategory;
import au.org.theark.core.model.study.entity.CustomFieldType;
import au.org.theark.core.model.study.entity.Study;
import au.org.theark.core.service.IArkCommonService;
import au.org.theark.core.util.CustomFieldCategoryOrderingHelper;
import au.org.theark.core.vo.ArkCrudContainerVO;
import au.org.theark.core.web.behavior.ArkDefaultFormFocusBehavior;
import au.org.theark.core.web.component.ArkDatePicker;
import au.org.theark.core.web.form.AbstractModalDetailForm;
import au.org.theark.lims.model.vo.BioCollectionCustomDataVO;
import au.org.theark.lims.model.vo.LimsVO;
import au.org.theark.lims.service.ILimsService;
import au.org.theark.lims.web.Constants;
import au.org.theark.lims.web.component.biocollectioncustomdata.BioCollectionCustomDataDataViewPanel;
import au.org.theark.lims.web.component.button.NumberOfLabelsPanel;
import au.org.theark.lims.web.component.button.zebra.biocollection.PrintBioCollectionLabelButton;
import au.org.theark.lims.web.component.button.zebra.biocollection.PrintBiospecimensForBioCollectionButton;

/**
 * @author cellis
 * 
 */
public class BioCollectionModalDetailForm extends AbstractModalDetailForm<LimsVO> {

	private static final long			serialVersionUID	= 2926069852602563767L;
	private static final Logger		log					= LoggerFactory.getLogger(BioCollectionModalDetailForm.class);
	@SpringBean(name = au.org.theark.core.Constants.ARK_COMMON_SERVICE)
	private IArkCommonService<Void>	iArkCommonService;

	@SpringBean(name = Constants.LIMS_SERVICE)
	private ILimsService					iLimsService;

	private TextField<String>			idTxtFld;
	private TextField<String>			biocollectionUidTxtFld;
	private TextField<String>			nameTxtFld;
	private TextArea<String>			commentsTxtAreaFld;
	private DateTextField				collectionDateTxtFld;
	private ModalWindow					modalWindow;
	private Panel 						bioCollectionCFDataEntryPanel;
	private AjaxButton					printBioCollectionLabelButton;
	private AjaxButton					printBiospecimensForBioCollectionButton;
	private AjaxButton					printStrawBiospecimensForBioCollectionButton;
	protected NumberOfLabelsPanel 		numberOfLabels;
	private DropDownChoice<CustomFieldCategory>		customeFieldCategoryDdc;
	

	/**
	 * Constructor
	 * 
	 * @param id
	 * @param feedBackPanel
	 * @param arkCrudContainerVo
	 * @param modalWindow
	 * @param containerForm
	 * @param detailPanelContainer
	 */
	public BioCollectionModalDetailForm(String id, FeedbackPanel feedBackPanel, ArkCrudContainerVO arkCrudContainerVo, ModalWindow modalWindow, CompoundPropertyModel<LimsVO> cpModel) {
		super(id, feedBackPanel, arkCrudContainerVo, cpModel);
		this.modalWindow = modalWindow;
		refreshEntityFromBackend();
	}

	protected void refreshEntityFromBackend() {
		// Get BioCollection entity fresh from backend
		BioCollection bioCollection = cpModel.getObject().getBioCollection();
		if (bioCollection.getId() != null) {
			try {
				cpModel.getObject().setBioCollection(iLimsService.getBioCollection(bioCollection.getId()));
			}
			catch (EntityNotFoundException e) {
				this.error("Can not edit this record - it has been invalidated (e.g. deleted)");
				log.error(e.getMessage());
			}
		}		
	}
	public void onBeforeRender(){
		Study study = this.getModelObject().getBioCollection().getStudy();
		
		if(study!=null && !study.getAutoGenerateBiocollectionUid()){
			biocollectionUidTxtFld.setEnabled(true);	
		}
		else{
			biocollectionUidTxtFld.setEnabled(false);
		}
			super.onBeforeRender();
	}
	/**
	 * 
	 */
	public void initialiseDetailForm() {
		idTxtFld = new TextField<String>("bioCollection.id");
		biocollectionUidTxtFld = new TextField<String>("bioCollection.biocollectionUid");
		nameTxtFld = new TextField<String>("bioCollection.name");
		
		commentsTxtAreaFld = new TextArea<String>("bioCollection.comments");
		collectionDateTxtFld = new DateTextField("bioCollection.collectionDate", au.org.theark.core.Constants.DD_MM_YYYY);
		ArkDatePicker datePicker = new ArkDatePicker();
		datePicker.bind(collectionDateTxtFld);
		collectionDateTxtFld.add(datePicker); 
		
		Collection<CustomFieldCategory> customFieldCategoryCollection=getAvailableAllCategoryListInStudyByCustomFieldType();
		List<CustomFieldCategory> customFieldCatLst=CustomFieldCategoryOrderingHelper.getInstance().orderHierarchicalyCustomFieldCategories((List<CustomFieldCategory>)customFieldCategoryCollection);
		ChoiceRenderer customfieldCategoryRenderer = new ChoiceRenderer(Constants.CUSTOMFIELDCATEGORY_NAME, Constants.CUSTOMFIELDCATEGORY_ID){
						@Override
						public Object getDisplayValue(Object object) {
						CustomFieldCategory cuscat=(CustomFieldCategory)object;
							return CustomFieldCategoryOrderingHelper.getInstance().preTextDecider(cuscat)+ super.getDisplayValue(object);
						}
		};
		customeFieldCategoryDdc = new DropDownChoice<CustomFieldCategory>(Constants.FIELDVO_CUSTOMFIELD_CUSTOEMFIELDCATEGORY, customFieldCatLst, customfieldCategoryRenderer);
		customeFieldCategoryDdc.setOutputMarkupId(true);
		customeFieldCategoryDdc.setNullValid(true);
		customeFieldCategoryDdc.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				arkCrudContainerVo.getDetailPanelFormContainer().remove(bioCollectionCFDataEntryPanel);
				bioCollectionCFDataEntryPanel = new BioCollectionCustomDataDataViewPanel("bioCollectionCFDataEntryPanel", changeDataModel(cpModel.getObject()))
						.initialisePanel(iArkCommonService.getUserConfig(au.org.theark.core.Constants.CONFIG_CUSTOM_FIELDS_PER_PAGE).getIntValue(),customeFieldCategoryDdc.getModelObject());
				//bioCollectionCFDataEntryPanel.setOutputMarkupId(true);
				arkCrudContainerVo.getDetailPanelFormContainer().add(bioCollectionCFDataEntryPanel);
				//target.add(bioCollectionCFDataEntryPanel);
				target.add(arkCrudContainerVo.getDetailPanelContainer());
				
			}
		});
		//initialiseBioCollectionCFDataEntry(null);
		bioCollectionCFDataEntryPanel = new BioCollectionCustomDataDataViewPanel("bioCollectionCFDataEntryPanel", changeDataModel(cpModel.getObject()))
				.initialisePanel(iArkCommonService.getUserConfig(au.org.theark.core.Constants.CONFIG_CUSTOM_FIELDS_PER_PAGE).getIntValue(),customeFieldCategoryDdc.getModelObject());
		//bioCollectionCFDataEntryPanel.setOutputMarkupId(true);
		BioCollection bioCollection = cpModel.getObject().getBioCollection();
		numberOfLabels = new NumberOfLabelsPanel("numberOfLabels");
		printBioCollectionLabelButton = new PrintBioCollectionLabelButton("printBioCollectionLabel", bioCollection, (IModel<Number>) numberOfLabels.getDefaultModel()) {

			private static final long	serialVersionUID	= 1L;

			@Override
			protected void onPostSubmit(AjaxRequestTarget target, Form<?> form) {
			}
		};
		printBioCollectionLabelButton.setDefaultFormProcessing(false);
		
		printBiospecimensForBioCollectionButton = new PrintBiospecimensForBioCollectionButton("printBiospecimensForBioCollectionButton", bioCollection, "zebra biospecimen", (IModel<Number>) numberOfLabels.getDefaultModel()) {

			private static final long	serialVersionUID	= 1L;

			@Override
			protected void onPostSubmit(AjaxRequestTarget target, Form<?> form) {
			}
		};
		printBioCollectionLabelButton.setDefaultFormProcessing(false);

		printStrawBiospecimensForBioCollectionButton = new PrintBiospecimensForBioCollectionButton("printStrawBiospecimensForBioCollectionButton", bioCollection, "straw barcode", (IModel<Number>) numberOfLabels.getDefaultModel()) {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onPostSubmit(AjaxRequestTarget target, Form<?> form) {
			}
		};
		printStrawBiospecimensForBioCollectionButton.setDefaultFormProcessing(false);
		
		attachValidators();
		addComponents();
		
		// Focus on Collection Date
		collectionDateTxtFld.add(new ArkDefaultFormFocusBehavior());
	}
	
	//private boolean initialiseBioCollectionCFDataEntry(CustomFieldCategory customFieldCategory) {
		//boolean replacePanel = false;
		//if (!(bioCollectionCFDataEntryPanel instanceof BioCollectionCustomDataDataViewPanel)) {
			//bioCollectionCFDataEntryPanel = new BioCollectionCustomDataDataViewPanel("bioCollectionCFDataEntryPanel", changeDataModel(cpModel.getObject())).initialisePanel(null,customFieldCategory);
			//replacePanel = true;
		//}
		//return replacePanel;
	//}
	/**
	 * Changed data model
	 * @param limsVO
	 * @return
	 */
	private CompoundPropertyModel<BioCollectionCustomDataVO> changeDataModel(LimsVO limsVO){
		CompoundPropertyModel<BioCollectionCustomDataVO> bioCFDataCpModel = new CompoundPropertyModel<BioCollectionCustomDataVO>(new BioCollectionCustomDataVO());		
		bioCFDataCpModel.getObject().setBioCollection(limsVO.getBioCollection());
		bioCFDataCpModel.getObject().setArkFunction(iArkCommonService.getArkFunctionByName(au.org.theark.core.Constants.FUNCTION_KEY_VALUE_LIMS_CUSTOM_FIELD));
		return bioCFDataCpModel;
	}

	protected void attachValidators() {
		idTxtFld.setRequired(true);
		biocollectionUidTxtFld.setRequired(true).setLabel(new StringResourceModel("error.bioCollection.biocollectionUid.required", this, new Model<String>("BioCollectionUid")));
		nameTxtFld.add(StringValidator.maximumLength(au.org.theark.core.Constants.GENERAL_FIELD_NAME_MAX_LENGTH_50));
	}

	private void addComponents() {
		arkCrudContainerVo.getDetailPanelFormContainer().add(idTxtFld.setEnabled(false));
		arkCrudContainerVo.getDetailPanelFormContainer().add(biocollectionUidTxtFld);
		arkCrudContainerVo.getDetailPanelFormContainer().add(nameTxtFld);
		arkCrudContainerVo.getDetailPanelFormContainer().add(commentsTxtAreaFld);
		arkCrudContainerVo.getDetailPanelFormContainer().add(collectionDateTxtFld);
		arkCrudContainerVo.getDetailPanelFormContainer().add(customeFieldCategoryDdc);
		arkCrudContainerVo.getDetailPanelFormContainer().add(bioCollectionCFDataEntryPanel);
		
		add(numberOfLabels);
		add(printBioCollectionLabelButton);
		add(printBiospecimensForBioCollectionButton);
		add(printStrawBiospecimensForBioCollectionButton);
		
		add(arkCrudContainerVo.getDetailPanelFormContainer());
	}


	@Override
	protected void onSave(AjaxRequestTarget target) {

		try {
			if (cpModel.getObject().getBioCollection().getId() == null) {
				// Save
				
				iLimsService.createBioCollection(cpModel.getObject());
				
				this.info("Biospecimen collection " + cpModel.getObject().getBioCollection().getBiocollectionUid() + " was created successfully");
				
			}
			else {
				// Update
				iLimsService.updateBioCollection(cpModel.getObject());
				this.info("Biospecimen collection " + cpModel.getObject().getBioCollection().getBiocollectionUid() + " was updated successfully");
				
			}
			if (bioCollectionCFDataEntryPanel instanceof BioCollectionCustomDataDataViewPanel) {
				((BioCollectionCustomDataDataViewPanel) bioCollectionCFDataEntryPanel).saveCustomData();
			}
			// refresh the CF data entry panel (if necessary)
			//if (initialiseBioCollectionCFDataEntry(customeFieldCategoryDdc.getModelObject()) == true) {
				//arkCrudContainerVo.getDetailPanelFormContainer().addOrReplace(bioCollectionCFDataEntryPanel);
			//}
			
			if (target != null) {
				onSavePostProcess(target);
			}
		} catch (ArkSystemException e) {
			this.error(e.getMessage());
		}
		
		if (target != null) {
			processErrors(target);
		}
		
	}

	@Override
	protected void onClose(AjaxRequestTarget target) {
		target.add(feedbackPanel);
		modalWindow.close(target);
	}

	@Override
	protected void onDeleteConfirmed(AjaxRequestTarget target, Form<?> form) {
		BioCollection bioCollection=cpModel.getObject().getBioCollection();
		//Ark-1606 bug fix for deleting bio-collection which has already data.  
		if(iLimsService.hasBiocllectionGotCustomFieldData(bioCollection)){
			this.error("Biospecimen collection " + bioCollection.getBiocollectionUid() + " can not be deleted because it has biocollection custom field data attached.");
			target.add(feedbackPanel);
		}
		if(iLimsService.hasBiospecimens(bioCollection)) {
			this.error("Biospecimen collection " + bioCollection.getBiocollectionUid() + " can not be deleted because there are biospecimens attached.");
			target.add(feedbackPanel);
		}
		if(!iLimsService.hasBiospecimens(bioCollection) &&(!iLimsService.hasBiocllectionGotCustomFieldData(bioCollection)))
		{
			iLimsService.deleteBioCollection(cpModel.getObject());
			this.info("Biospecimen collection " + bioCollection.getBiocollectionUid() + " was deleted successfully.");
			onClose(target);
		}
		
	}

	@Override
	protected void processErrors(AjaxRequestTarget target) {
		target.add(feedbackPanel);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.org.theark.core.web.form.AbstractDetailForm#isNew()
	 */
	@Override
	protected boolean isNew() {
		if (cpModel.getObject().getBioCollection().getId() == null) {
			return true;
		}
		else {
			return false;
		}
	}
	/**
	 * Get custom field category collection from model.
	 * @return
	 */
	private Collection<CustomFieldCategory> getAvailableAllCategoryListInStudyByCustomFieldType(){
		
		Study study =cpModel.getObject().getBioCollection().getStudy();
		ArkFunction arkFunction=iArkCommonService.getArkFunctionByName(au.org.theark.core.Constants.FUNCTION_KEY_VALUE_LIMS_CUSTOM_FIELD_CATEGORY);
		
		CustomFieldType customFieldType=iArkCommonService.getCustomFieldTypeByName(au.org.theark.core.Constants.BIOCOLLECTION);
		Collection<CustomFieldCategory> customFieldCategoryCollection = null;
		try {
			customFieldCategoryCollection =  iArkCommonService.getAvailableAllCategoryListInStudyByCustomFieldType(study,arkFunction, customFieldType);
		} catch (ArkSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return customFieldCategoryCollection;
	}
	
}
