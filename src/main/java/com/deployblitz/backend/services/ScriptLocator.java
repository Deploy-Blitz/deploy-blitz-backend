package com.deployblitz.backend.services;

import java.io.IOException;

public interface ScriptLocator {

    String getScript(String gitDirectoryPath) throws IOException;

    Boolean repositoryHasScript(String gitDirectoryPath) throws IOException;

}
