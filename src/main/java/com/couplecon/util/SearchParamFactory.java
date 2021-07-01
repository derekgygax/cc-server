package com.couplecon.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.couplecon.data.Couple;
import com.couplecon.data.SearchParam;
import com.couplecon.data.SurveyChoice;
import com.couplecon.data.SurveyQuestion;

public class SearchParamFactory {
	HashMap<String,String> nameToTable;
	HashMap<String,String> nameToColumn;
	HashMap<String,String> nameToType;
	HashMap<String,String> nameToTitle;
	HashMap<String,String> nameToGroup;
	HashSet<String> validNames;
	Couple homeCouple;
	
	public SearchParamFactory() {
		this.validNames = new HashSet<String>();
		this.nameToTable = new HashMap<String,String>();
		this.nameToColumn = new HashMap<String,String>();
		this.nameToType = new HashMap<String,String>();
		this.nameToTitle = new HashMap<String,String>();
		this.nameToGroup = new HashMap<String,String>();
		
		this.validNames.add("age");
		this.nameToTable.put("age", "partner");
		this.nameToColumn.put("age", "age");
		this.nameToType.put("age", "range");
		this.nameToTitle.put("age","Age");
		this.nameToGroup.put("age", "basic");
		
		this.validNames.add("numChildren");
		this.nameToTable.put("numChildren", "couple");
		this.nameToColumn.put("numChildren", "num_children");
		this.nameToType.put("numChildren", "range");
		this.nameToTitle.put("numChildren","Number of children");
		this.nameToGroup.put("numChildren", "basic");
		
		this.validNames.add("childrenAtHome");
		this.nameToTable.put("childrenAtHome", "couple");
		this.nameToColumn.put("childrenAtHome", "children_at_home");
		this.nameToType.put("childrenAtHome", "multiselect");
		this.nameToTitle.put("childrenAtHome","Children at home");
		this.nameToGroup.put("childrenAtHome", "advanced");
		
		this.validNames.add("relationshipType");
		this.nameToTable.put("relationshipType", "couple");
		this.nameToColumn.put("relationshipType", "relationship_type");
		this.nameToType.put("relationshipType", "multiselect");
		this.nameToTitle.put("relationshipType","Relationship type");
		this.nameToGroup.put("relationshipType", "basic");

		this.validNames.add("maxDistance");
		this.nameToTable.put("maxDistance", "couple");
		this.nameToColumn.put("maxDistance", "location");
		this.nameToType.put("maxDistance", "singleselect");
		this.nameToTitle.put("maxDistance","Max Distance");
		this.nameToGroup.put("maxDistance", "basic");

		this.validNames.add("race");
		this.nameToTable.put("race", "partner");
		this.nameToColumn.put("race", "race");
		this.nameToType.put("race", "multiselect");
		this.nameToTitle.put("race","Race");
		this.nameToGroup.put("race", "advanced");
	}

	public SearchParamFactory(String searchingPartner) throws Exception {
		this();
		this.homeCouple = DB.getCouple(DB.getCoupleId(searchingPartner));
	}
	
	public ArrayList<SearchParam> getAvailableParams() throws Exception {
		ArrayList<SearchParam> out = new ArrayList<SearchParam>();
		for (String name : this.validNames) {
			String type = this.nameToType.get(name);
			String table = this.nameToTable.get(name);
			String title = this.nameToTitle.get(name);
			String group = this.nameToGroup.get(name);
			SearchParam param = new SearchParam();
			param.setId(name);
			param.setType(type);
			param.setTitle(title);
			param.setGroup(group);
			switch (type) {
			case "range":
				switch (name) {
				case "age":
					param.setMin(18);
					param.setMax(100);
					break;
				case "numChildren":
					param.setMin(0);
					param.setMax(10); //TODO what is a reasonable max? get from db?
					break;
				}
				break;
			case "multiselect":
				ArrayList<SurveyQuestion> questions = DB.getCategoricalQuestions(table.substring(0,1).toLowerCase());
				for (SurveyQuestion question : questions) {
					if (name.equals(question.id)) {
						for (SurveyChoice choice : question.choices) {
							param.addValue(choice.text);
						}
					}
				}
				break;
			case "singleselect":
				switch (name) {
				case "maxDistance":
					param.addValue("1");
					param.addValue("5");
					param.addValue("10");
					param.addValue("20");
					param.addValue("50");
					break;
				}
				break;
			}
			out.add(param);
		}
		return out;
	}
	
	public boolean isValidName(String paramName) {
		return this.validNames.contains(paramName);
	}
	
	public SearchParam makeSearchParam(String name, String[] vals) {
		SearchParam param = new SearchParam();
		param.setId(name);
		String type = this.nameToType.get(name);
		String table = this.nameToTable.get(name);
		param.setType(type);
		param.setTable(table);
		param.setColumn(this.nameToColumn.get(name));
		switch (type) {
		case "range":
			param.setMin(Integer.valueOf(vals[0]));
			param.setMax(Integer.valueOf(vals[1]));
			if (table.equals("partner")) {
				param.setBothPartners(true);
			}
			break;
		case "multiselect":
			for (int i=0; i < vals.length; i++) {
				param.addValue(vals[i]);
			}
			break;
		case "singleselect":
			param.addValue(vals[0]);
		}
		if ("maxDistance".equals(name)) {
			param.setCenter(this.homeCouple.getLocation());
		}
		return param;
	}
}
