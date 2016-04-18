package com.orientechnologies.orient.etl.extractor;

public abstract class OextractorSuper extends OAbstractExtractor {

	protected String url;
	protected String userName;
	protected String userPassword;
	protected String queryCount;

	public OextractorSuper() {
		super();
	}

	@Override
	public String getName() {
	    return "jdbc";
	  }

}