package com.concurrent_programming.amogus.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageExample {
    private String senderName;
    private String receiverName;
    private String message;
    private String date;
    private Status status;
}
