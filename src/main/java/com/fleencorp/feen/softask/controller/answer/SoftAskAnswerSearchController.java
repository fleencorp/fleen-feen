package com.fleencorp.feen.softask.controller.answer;

import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.softask.exception.core.SoftAskAnswerNotFoundException;
import com.fleencorp.feen.softask.model.domain.SoftAskAnswer;
import com.fleencorp.feen.softask.model.request.SoftAskSearchRequest;
import com.fleencorp.feen.softask.model.search.SoftAskAnswerSearchResult;
import com.fleencorp.feen.softask.service.answer.SoftAskAnswerSearchService;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/softask/answer")
public class SoftAskAnswerSearchController {

  private final SoftAskAnswerSearchService softAskAnswerSearchService;

  public SoftAskAnswerSearchController(final SoftAskAnswerSearchService softAskAnswerSearchService) {
    this.softAskAnswerSearchService = softAskAnswerSearchService;
  }

  @Operation(summary = "Retrieve a specific soft ask answer",
    description = "Fetches the details of a soft ask answer by its ID.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the answer",
      content = @Content(schema = @Schema(implementation = SoftAskAnswer.class))),
    @ApiResponse(responseCode = "404", description = "Answer not found",
      content = @Content(schema = @Schema(implementation = SoftAskAnswerNotFoundException.class))),
    @ApiResponse(responseCode = "400", description = "Failed operation",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @GetMapping(value = "/detail/{answerId}")
  public SoftAskAnswer findSoftAskAnswer(
    @Parameter(description = "ID of the answer to retrieve", required = true)
      @PathVariable(name = "answerId") final Long answerId) {
    return softAskAnswerSearchService.findSoftAskAnswer(answerId);
  }

  @Operation(summary = "Search for soft ask answers",
    description = "Searches for soft ask answers based on the provided search criteria.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully retrieved search results",
      content = @Content(schema = @Schema(implementation = SoftAskAnswerSearchResult.class))),
    @ApiResponse(responseCode = "400", description = "Failed operation",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @GetMapping(value = "/find-answers")
  public SoftAskAnswerSearchResult findSoftAskAnswers(
    @Parameter(description = "Search criteria for answers", required = true)
      @SearchParam final SoftAskSearchRequest searchRequest,
    @Parameter(hidden = true)
      @AuthenticationPrincipal final RegisteredUser user) {
    return softAskAnswerSearchService.findSoftAskAnswers(searchRequest, user);
  }
} 