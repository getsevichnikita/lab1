package com.library.model;

import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReaderDTONplus1 {
    private Long id;
    private String name;
    private List<LoanDTO> loans;
}
