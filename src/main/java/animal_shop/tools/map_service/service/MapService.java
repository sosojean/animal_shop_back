package animal_shop.tools.map_service.service;

import animal_shop.tools.map_service.dto.MapDTO;
import animal_shop.tools.map_service.dto.MapDTOResponse;
import animal_shop.tools.map_service.repository.MapRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class MapService {

    @Autowired
    private MapRepository mapRepository;

    @Value("${openApi.serviceKey}")
    private String serviceKey;

    @Value("${openApi.endPoint}")
    private String endPoint;

    @Value("${openApi.dataType}")
    private String dataType;

    private long totalPages;

    private long totalCount;
    public ResponseEntity<?> mapFind() {

        mapRepository.deleteAll();
        int pageNo = 1; // 페이지 번호 초기화
        List<MapDTO> resultList = new ArrayList<>(); // 결과를 담을 리스트
        totalCount = 0; // 총 항목 수
        totalPages = Long.MAX_VALUE; // 최대 페이지를 추후 계산

        RestTemplate restTemplate = new RestTemplate();

        while (pageNo <= totalPages) {
            String API_URL = endPoint + "?serviceKey=" + serviceKey + "&dataType=" + dataType +
                    "&perPage=1000&pageNo=" + pageNo;

            log.info("API 호출 URL: {}", API_URL);
            log.info("전체 페이지 : {}", totalPages);

            try {
                // API 호출 및 응답 처리
                String response = restTemplate.getForObject(API_URL, String.class);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(response);

                // total_count가 존재하면 최대 페이지 계산
                JsonNode totalCountNode = rootNode.path("totalCount");
                if (totalCountNode.isNumber()) {
                    totalCount = totalCountNode.asLong();
                    totalPages = (totalCount + 1000 - 1) / 1000; // 총 페이지 계산
                }

                // 데이터 처리
                JsonNode dataNode = rootNode.path("data");
                if (dataNode.isArray() && dataNode.size() > 0) {
                    for (JsonNode itemNode : dataNode) {
                        MapDTO item = objectMapper.treeToValue(itemNode, MapDTO.class);
                        resultList.add(item);

                        // 엔티티로 변환하여 DB 저장
                        mapRepository.save(item.toEntity());
                    }
                    log.info("현재 페이지 처리 완료: {}", pageNo);
                    pageNo++; // 다음 페이지로 이동
                } else {
                    log.info("더 이상 데이터가 없습니다. 반복 종료.");
                    break;
                }

            } catch (Exception e) {
                log.error("API 응답 처리 중 오류 발생: ", e);
                break; // 오류 발생 시 반복 종료
            }
        }

        // 결과 로그 확인
        log.info("총 항목 수: {}", totalCount);
        log.info("수집된 데이터 수: {}", resultList.size());

        MapDTOResponse response = MapDTOResponse.builder()
                .MapDTOList(resultList)
                .total_count(totalCount)
                .build();

        return ResponseEntity.ok().body(response);
    }

}
