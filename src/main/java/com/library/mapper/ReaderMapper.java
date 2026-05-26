package com.library.mapper;

import com.library.model.Loan;
import com.library.model.Reader;
import com.library.model.ReaderDTO;
import com.library.model.ReaderDTONplus1;

import java.util.List;

public class ReaderMapper {
    private ReaderMapper() {}
    public static ReaderDTO toDto(Reader reader) {
        if (reader == null) return null;

        ReaderDTO dto = new ReaderDTO();
        dto.setId(reader.getId());
        dto.setName(reader.getName());

        dto.setLoanIds(
                reader.getLoans()
                        .stream()
                        .map(Loan::getId)
                        .toList()
        );

        return dto;
    }

    public static Reader toEntity(ReaderDTO dto, List<Loan> loans) {
        if (dto == null) return null;

        Reader reader = new Reader();
        reader.setId(dto.getId());
        reader.setName(dto.getName());
        reader.setLoans(loans);

        return reader;
    }
    public static ReaderDTONplus1 toDtoNplus1(Reader reader) {
        return ReaderDTONplus1.builder()
                .id(reader.getId())
                .name(reader.getName())
                .loans(
                        reader.getLoans().stream()
                                .map(LoanMapper::toDtoFields)
                                .toList()
                )
                .build();
    }

}
