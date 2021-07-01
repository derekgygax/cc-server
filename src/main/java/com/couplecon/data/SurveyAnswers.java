package com.couplecon.data;

import java.util.HashMap;

public class SurveyAnswers {
	// This applyToBothPartners currently will only apply for the essentials questions
	// This is determines in SurveyQA
	public Boolean applyToBothPartners;
	public HashMap<String, SurveyChoice[]> answers;
}
