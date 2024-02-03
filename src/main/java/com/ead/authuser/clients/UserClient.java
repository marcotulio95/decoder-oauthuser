package com.ead.authuser.clients;

import com.ead.authuser.dtos.CourseDTO;
import com.ead.authuser.dtos.ResponsePageDTO;
import com.ead.authuser.utils.UtilsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.UUID;

@Component
@Log4j2
public class UserClient {
    private final static String REQUEST_URI = "http://localhost:8088";
    @Autowired
    private RestTemplate restTemplate;

    public Page<CourseDTO> getAllCoursesByUser(UUID userId, Pageable pageable) {
        ResponseEntity<ResponsePageDTO<CourseDTO>> response = null;
        final String requestUrl = REQUEST_URI + UtilsService.createUrlGetAllCoursesByUser(userId, pageable);
        log.debug("RequestUrl {}", requestUrl);
        log.info("RequestUrl {}", requestUrl);
        try {
            ParameterizedTypeReference<ResponsePageDTO<CourseDTO>> responseType = new ParameterizedTypeReference<ResponsePageDTO<CourseDTO>>() {
            };
            response = restTemplate.exchange(requestUrl, HttpMethod.GET, null, responseType);
            int numberOfElements = response.getBody() != null ? response.getBody().getSize() : 0;
            log.debug("Response number of elements: {}", numberOfElements);
        } catch (Exception e) {
            log.info("Error request /courses");
        }
        log.info("Ending request /courses userId {}", userId);
        return Objects.requireNonNull(response).getBody();
    }
}
