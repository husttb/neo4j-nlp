/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.graphaware.nlp.processor;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import java.util.Properties;

public class PipelineBuilder {
    private static final String CUSTOM_STOP_WORD_LIST = "start,starts,period,periods,a,an,and,are,as,at,be,but,by,for,if,in,into,is,it,no,not,of,o,on,or,such,that,the,their,then,there,these,they,this,to,was,will,with";

    private final Properties properties = new Properties();
    private final StringBuilder annotattors = new StringBuilder(); //basics annotators
    private int threadsNumber = 4;

    public PipelineBuilder tokenize() {
        checkForExistingAnnotators();
        annotattors.append("tokenize, ssplit, pos, lemma, ner");
        return this;
    }

    private void checkForExistingAnnotators() {
        if (annotattors.toString().length() > 0) {
            annotattors.append(", ");
        }
    }

    public PipelineBuilder extractSentiment() {
        checkForExistingAnnotators();
        annotattors.append("parse, sentiment");
        return this;
    }

    public PipelineBuilder extractRelations() {
        checkForExistingAnnotators();
        annotattors.append("relation");
        return this;
    }

    public PipelineBuilder extractCoref() {
        checkForExistingAnnotators();
        annotattors.append("mention, coref");
        properties.setProperty("coref.doClustering", "true");
        properties.setProperty("coref.md.type", "rule");
        properties.setProperty("coref.mode", "statistical");
        return this;
    }

    public PipelineBuilder defaultStopWordAnnotator() {
        checkForExistingAnnotators();
        annotattors.append("stopword");
        properties.setProperty("customAnnotatorClass.stopword", "com.graphaware.nlp.processor.StopwordAnnotator");
        properties.setProperty(StopwordAnnotator.STOPWORDS_LIST, CUSTOM_STOP_WORD_LIST);
        return this;
    }

    public PipelineBuilder stopWordAnnotator(Properties properties) {
        properties.entrySet().stream().forEach((entry) -> {
            this.properties.setProperty((String) entry.getKey(), (String) entry.getValue());
        });
        return this;
    }

    public PipelineBuilder threadNumber(int threads) {
        this.threadsNumber = threads;
        return this;
    }

    public StanfordCoreNLP build() {
        properties.setProperty("annotators", annotattors.toString());
        properties.setProperty("threads", String.valueOf(threadsNumber));
        StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
        return pipeline;
    }
}