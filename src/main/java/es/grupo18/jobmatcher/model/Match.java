package es.grupo18.jobmatcher.model;

public class Match {
    private Long id;
    private Long userId;
    private Long accountId;
    private String timestamp;

    public Match() {}

    public Match(Long userId, Long accountId) {
        this.userId = userId;
        this.accountId = accountId;
        this.timestamp = java.time.LocalDateTime.now().toString();
    }

    // Getters

    public Long getId() { return id; }
    public Long getAccountId() { return userId; }
    public Long getCompanyId() { return accountId; }
    public String getTimestamp() { return timestamp; }

    // Setters

    public void setId(Long id) { this.id = id; }
    public void setAccountId(Long userId) { this.userId = userId; }
    public void setCompanyId(Long accountId) { this.accountId = accountId; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

}
