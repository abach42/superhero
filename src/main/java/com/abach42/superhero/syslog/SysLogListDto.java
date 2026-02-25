package com.abach42.superhero.syslog;

import java.util.List;

public record SysLogListDto(List<SysLog> content, int page, long size, long totalElements) {
}
