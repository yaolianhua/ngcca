package io.hotcloud.allinone.statistics;

import io.hotcloud.common.PageResult;
import io.hotcloud.common.Pageable;
import io.hotcloud.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static io.hotcloud.common.WebResponse.ok;
import static io.hotcloud.common.WebResponse.okPage;

/**
 * @author yaolianhua789@gmail.com
 **/
@RestController
@RequestMapping("/v1/statistics")
@Tag(name = "Statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/{userid}")
    @Operation(
            summary = "statistics query",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "userid", description = "userid queried")
            }
    )
    public ResponseEntity<Result<Statistics>> statistics(@PathVariable("userid") String userid) {
        Statistics statistics = statisticsService.statistics(userid);
        return ok(statistics);
    }

    @GetMapping
    @Operation(
            summary = "all users statistics paging query",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "page", description = "current page", schema = @Schema(defaultValue = "1")),
                    @Parameter(name = "page_size", description = "pageSize", schema = @Schema(defaultValue = "10"))
            }
    )
    public ResponseEntity<PageResult<Statistics>> statistics(@RequestParam(value = "page", required = false) Integer page,
                                                             @RequestParam(value = "page_size", required = false) Integer pageSize) {
        PageResult<Statistics> pageResult = statisticsService.statistics(Pageable.of(page, pageSize));
        return okPage(pageResult);
    }
}
