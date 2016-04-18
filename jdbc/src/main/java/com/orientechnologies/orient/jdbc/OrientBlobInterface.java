package com.orientechnologies.orient.jdbc;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;

public interface OrientBlobInterface {

	/*
	   * (non-Javadoc)
	   * 
	   * @see java.sql.Blob#length()
	   */
	long length() throws SQLException;

	/*
	   * (non-Javadoc)
	   * 
	   * @see java.sql.Blob#getBytes(long, int)
	   */
	byte[] getBytes(long pos, int length) throws SQLException;

	/*
	   * (non-Javadoc)
	   * 
	   * @see java.sql.Blob#getBinaryStream()
	   */
	InputStream getBinaryStream() throws SQLException;

	/*
	   * (non-Javadoc)
	   * 
	   * @see java.sql.Blob#position(byte[], long)
	   */
	long position(byte[] pattern, long start) throws SQLException;

	/*
	   * (non-Javadoc)
	   * 
	   * @see java.sql.Blob#position(java.sql.Blob, long)
	   */
	long position(Blob pattern, long start) throws SQLException;

	/*
	   * (non-Javadoc)
	   * 
	   * @see java.sql.Blob#setBytes(long, byte[])
	   */
	int setBytes(long pos, byte[] bytes) throws SQLException;

	/*
	   * (non-Javadoc)
	   * 
	   * @see java.sql.Blob#setBytes(long, byte[], int, int)
	   */
	int setBytes(long pos, byte[] bytes, int offset, int len) throws SQLException;

	/*
	   * (non-Javadoc)
	   * 
	   * @see java.sql.Blob#setBinaryStream(long)
	   */
	OutputStream setBinaryStream(long pos) throws SQLException;

	/*
	   * (non-Javadoc)
	   * 
	   * @see java.sql.Blob#truncate(long)
	   */
	void truncate(long len) throws SQLException;

	/*
	   * (non-Javadoc)
	   * 
	   * @see java.sql.Blob#free()
	   */
	void free() throws SQLException;

	/*
	   * (non-Javadoc)
	   * 
	   * @see java.sql.Blob#getBinaryStream(long, long)
	   */
	InputStream getBinaryStream(long pos, long length) throws SQLException;

}