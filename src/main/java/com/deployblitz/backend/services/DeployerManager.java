package com.deployblitz.backend.services;

import com.deployblitz.backend.domain.dto.request.WebHookCreateRequestDto;
import com.deployblitz.backend.domain.dto.response.WebhookCreateResponseDto;
import com.deployblitz.backend.utils.HttpResponse;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.TransportException;

import java.io.IOException;

public interface DeployerManager {

    HttpResponse<WebhookCreateResponseDto> createWebhook(WebHookCreateRequestDto WebHookCreateRequestDto) throws GitAPIException, TransportException;

    HttpResponse<?> invokeWebhook(String name) throws GitAPIException, IOException;
}
