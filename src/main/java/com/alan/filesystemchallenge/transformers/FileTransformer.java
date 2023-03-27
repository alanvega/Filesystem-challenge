package com.alan.filesystemchallenge.transformers;

import com.alan.filesystemchallenge.models.responses.FileResponse;
import com.alan.filesystemchallenge.models.entities.File;
import org.springframework.stereotype.Component;

@Component
public class FileTransformer {

	public FileResponse transformFileResponse(File file) {
		var fileResponse = new FileResponse();
		fileResponse.setId(file.getId());
		fileResponse.setName(file.getName());
		fileResponse.setCreateDate(file.getCreateDate());
		return fileResponse;
	}
}
