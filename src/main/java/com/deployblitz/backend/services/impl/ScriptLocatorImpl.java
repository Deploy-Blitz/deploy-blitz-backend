package com.deployblitz.backend.services.impl;

import com.deployblitz.backend.services.ScriptLocator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class ScriptLocatorImpl implements ScriptLocator {
    @Value("${deploy-blitz-script-path}")
    private String scriptPath;

    @Value("${deploy-blitz-script-name}")
    private String scriptName;


    @Override
    public String getScript(String gitDirectoryPath) throws IOException {
        var deployBlitzShPath = gitDirectoryPath + "/" + scriptPath + scriptName;
        var scriptAsString = FileUtils.readFileToString(new File(deployBlitzShPath), StandardCharsets.UTF_8);
        if (scriptAsString != null) {
            return scriptAsString;
        }
        FileUtils.deleteQuietly(new File(deployBlitzShPath));
        throw new FileNotFoundException("File or Directory not found" + deployBlitzShPath);
    }

    @Override
    public Boolean repositoryHasScript(String gitDirectoryPath) throws IOException {
        var deployBlitzShPath = gitDirectoryPath + "/" + scriptPath + scriptName;
        var scriptAsString = FileUtils.readFileToString(new File(deployBlitzShPath), StandardCharsets.UTF_8);
        if (scriptAsString != null) {
            return true;
        }
        FileUtils.deleteQuietly(new File(deployBlitzShPath));
        return false;
    }
}
