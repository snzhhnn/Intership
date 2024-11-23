package model;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Schema {
    private int id;
    private int version;
    private String description;
    private String script;
    private String type;
    private String checksum;
    private String installedBy;
    private Boolean success;
    private String sql;
}