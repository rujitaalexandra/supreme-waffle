package com.sporty.settlement.controller;

import com.sporty.settlement.domain.Bet;
import com.sporty.settlement.service.BetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BetController.class)
class BetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BetService betService;

    @Test
    void addBet_shouldReturn200_whenValidRequest() throws Exception {
        doNothing().when(betService).save(any(Bet.class));

        mockMvc.perform(put("/bets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "betId": "bet-1",
                                    "userId": "user-1",
                                    "eventId": "event-1",
                                    "eventMarketId": "market-1",
                                    "eventWinnerId": "winner-1",
                                    "betAmount": "100"
                                }
                                """))
                .andExpect(status().isOk());

        verify(betService).save(new Bet("bet-1", "user-1", "event-1", "market-1", "winner-1", "100"));
    }

    @Test
    void addBet_shouldReturn400_whenBodyIsMissing() throws Exception {
        mockMvc.perform(put("/bets")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllBets_shouldReturnListOfBets() throws Exception {
        when(betService.findAll()).thenReturn(List.of(
                new Bet("bet-1", "user-1", "event-1", "market-1", "winner-1", "100"),
                new Bet("bet-2", "user-2", "event-2", "market-2", "winner-2", "200")
        ));

        mockMvc.perform(get("/bets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].betId").value("bet-1"))
                .andExpect(jsonPath("$[0].userId").value("user-1"))
                .andExpect(jsonPath("$[0].eventId").value("event-1"))
                .andExpect(jsonPath("$[0].eventMarketId").value("market-1"))
                .andExpect(jsonPath("$[0].eventWinnerId").value("winner-1"))
                .andExpect(jsonPath("$[0].betAmount").value("100"))
                .andExpect(jsonPath("$[1].betId").value("bet-2"));
    }

    @Test
    void getAllBets_shouldReturnEmptyList_whenNoBetsInRedis() throws Exception {
        when(betService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/bets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
