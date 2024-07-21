package com.testing.springpractice.exception;

public class AllocationExceededException extends RuntimeException  {
    private final Long portfolioId;

    public AllocationExceededException(Long portfolioId, String message) {
        super(message);
        this.portfolioId = portfolioId;
    }

    public Long getPortfolioId() {
        return portfolioId;
    }
}
