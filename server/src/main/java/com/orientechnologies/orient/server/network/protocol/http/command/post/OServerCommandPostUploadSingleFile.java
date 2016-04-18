/*
 *
 * Copyright 2011 Luca Molino (molino.luca--AT--gmail.com *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.orientechnologies.orient.server.network.protocol.http.command.post;

import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;

import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.serialization.serializer.OJSONWriter;
import com.orientechnologies.orient.server.network.protocol.http.OHttpRequest;
import com.orientechnologies.orient.server.network.protocol.http.OHttpResponse;
import com.orientechnologies.orient.server.network.protocol.http.OHttpUtils;
import com.orientechnologies.orient.server.network.protocol.http.multipart.OHttpMultipartContentBaseParser;
import com.orientechnologies.orient.server.network.protocol.http.multipart.OHttpMultipartFileToRecordContentParser;
import com.orientechnologies.orient.server.network.protocol.http.multipart.OHttpMultipartRequestCommand;

/**
 * @author luca.molino
 * 
 */
public class OServerCommandPostUploadSingleFile extends OHttpMultipartRequestCommand<String, ORID> {

  private static final String[]       NAMES = { "POST|uploadSingleFile/*" };

  protected StringWriter              buffer;
  protected OJSONWriter               writer;
  protected ORID                      fileRID;
  protected OServerCommandData data = new OServerCommandData();

protected long                      now;
  protected ODatabaseDocumentInternal database;

  @Override
  public boolean execute(final OHttpRequest iRequest, OHttpResponse iResponse) throws Exception {
    if (!iRequest.isMultipart) {
      iResponse.send(OHttpUtils.STATUS_INVALIDMETHOD_CODE, "Request is not multipart/form-data", OHttpUtils.CONTENT_TEXT_PLAIN,
          "Request is not multipart/form-data", null);
    } else if (iRequest.multipartStream == null || iRequest.multipartStream.available() <= 0) {
      iResponse.send(OHttpUtils.STATUS_INVALIDMETHOD_CODE, "Content stream is null or empty", OHttpUtils.CONTENT_TEXT_PLAIN,
          "Content stream is null or empty", null);
    } else {
      database = getProfiledDatabaseInstance(iRequest);
      try {
        buffer = new StringWriter();
        writer = new OJSONWriter(buffer);
        writer.beginObject();
        parse(iRequest, iResponse, new OHttpMultipartContentBaseParser(), new OHttpMultipartFileToRecordContentParser(), database);
        boolean ok = saveRecord(iRequest, iResponse);
        writer.endObject();
        writer.flush();
        if (ok) {
          iResponse.send(OHttpUtils.STATUS_OK_CODE, "OK", OHttpUtils.CONTENT_JSON, buffer.toString(), null);
        }
      } finally {
        if (database != null)
          database.close();
        database = null;
        if (buffer != null)
          buffer.close();
        buffer = null;
        if (writer != null)
          writer.close();
        writer = null;
        data.fileDocument = null;
        data.fileName = null;
        data.fileType = null;
        if (fileRID != null)
          fileRID.reset();
        fileRID = null;
      }
    }
    return false;
  }

  @Override
  protected void processBaseContent(OHttpRequest iRequest, String iContentResult, HashMap<String, String> headers) throws Exception {
    if (headers.containsKey(OHttpUtils.MULTIPART_CONTENT_NAME)
        && headers.get(OHttpUtils.MULTIPART_CONTENT_NAME).equals(getDocumentParamenterName())) {
      data.fileDocument = iContentResult;
    }
  }

  @Override
  protected void processFileContent(OHttpRequest iRequest, ORID iContentResult, HashMap<String, String> headers) throws Exception {
    if (headers.containsKey(OHttpUtils.MULTIPART_CONTENT_NAME)
        && headers.get(OHttpUtils.MULTIPART_CONTENT_NAME).equals(getFileParamenterName())) {
      fileRID = iContentResult;
      if (headers.containsKey(OHttpUtils.MULTIPART_CONTENT_FILENAME)) {
        data.fileName = headers.get(OHttpUtils.MULTIPART_CONTENT_FILENAME);
        if (data.fileName.charAt(0) == '"') {
          data.fileName = new String(data.fileName.substring(1));
        }
        if (data.fileName.charAt(data.fileName.length() - 1) == '"') {
          data.fileName = new String(data.fileName.substring(0, data.fileName.length() - 1));
        }
        data.fileType = headers.get(OHttpUtils.MULTIPART_CONTENT_TYPE);

        final Calendar cal = Calendar.getInstance();
        final DateFormat formatter = database.getStorage().getConfiguration().getDateFormatInstance();
        now = cal.getTimeInMillis();

        writer.beginObject("uploadedFile");
        writer.writeAttribute(1, true, "name", data.fileName);
        writer.writeAttribute(1, true, "type", data.fileType);
        writer.writeAttribute(1, true, "date", formatter.format(cal.getTime()));
        writer.writeAttribute(1, true, "rid", fileRID);
        writer.endObject();
      }
    }
  }

  public boolean saveRecord(OHttpRequest iRequest, final OHttpResponse iResponse) throws InterruptedException, IOException {
    if (data.fileDocument != null) {
      if (fileRID != null) {
        if (data.fileDocument.contains("$now")) {
          data.fileDocument = data.fileDocument.replace("$now", String.valueOf(now));
        }
        if (data.fileDocument.contains("$fileName")) {
          data.fileDocument = data.fileDocument.replace("$fileName", data.fileName);
        }
        if (data.fileDocument.contains("$fileType")) {
          data.fileDocument = data.fileDocument.replace("$fileType", data.fileType);
        }
        if (data.fileDocument.contains("$file")) {
          data.fileDocument = data.fileDocument.replace("$file", fileRID.toString());
        }
        ODocument doc = new ODocument();
        doc.fromJSON(data.fileDocument);
        doc.save();
        writer.beginObject("updatedDocument");
        writer.writeAttribute(1, true, "rid", doc.getIdentity().toString());
        writer.endObject();
      } else {
        iResponse.send(OHttpUtils.STATUS_INVALIDMETHOD_CODE, "File cannot be null", OHttpUtils.CONTENT_TEXT_PLAIN,
            "File cannot be null", null);
        return false;
      }

      data.fileDocument = null;
    } else {
      if (fileRID == null) {
        iResponse.send(OHttpUtils.STATUS_INVALIDMETHOD_CODE, "File cannot be null", OHttpUtils.CONTENT_TEXT_PLAIN,
            "File cannot be null", null);
        return false;
      }
    }
    return true;
  }

  @Override
  protected String getDocumentParamenterName() {
    return "linkValue";
  }

  @Override
  protected String getFileParamenterName() {
    return "file";
  }

  @Override
  public String[] getNames() {
    return NAMES;
  }

}
