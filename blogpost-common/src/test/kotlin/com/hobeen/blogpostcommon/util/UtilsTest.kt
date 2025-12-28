package com.hobeen.blogpostcommon.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import java.time.LocalDateTime
import kotlin.test.Test

class UtilsTest {

    @Test
    @DisplayName("localdatetime 변환")
    fun localDateParseTest() {
        //given
        val date1_1Str = "12월 9, 2025"
        val date1_2Str = "12월 19, 2025"

        //when
        val date1_1 = localDateParse(date1_1Str)
        val date1_2 = localDateParse(date1_2Str)

        //then
        assertThat(date1_1).isEqualTo(LocalDateTime.of(2025, 12, 9, 0, 0, 0))
        assertThat(date1_2).isEqualTo(LocalDateTime.of(2025, 12, 19, 0, 0, 0))
    }

    @Test
    @DisplayName("localdatetime 변환 6")
    fun localDateParseTest6() {
        //given
        val date1_1Str = "Tue, 16 Dec 2025 15:19:27"
        val date1_2Str = "Mon, 8 Dec 2025 15:01:27"

        //when
        val date1_1 = localDateParse(date1_1Str)
        val date1_2 = localDateParse(date1_2Str)

        //then
        assertThat(date1_1).isEqualTo(LocalDateTime.of(2025, 12, 16, 15, 19, 27))
        assertThat(date1_2).isEqualTo(LocalDateTime.of(2025, 12, 8, 15, 1, 27))
    }

    @Test
    @DisplayName("localdatetime 변환 7")
    fun localDateParseTest7() {
        //given
        val date1_1Str = "December 10, 2025"
        val date1_2Str = "November 1, 2025"

        //when
        val date1_1 = localDateParse(date1_1Str)
        val date1_2 = localDateParse(date1_2Str)

        //then
        assertThat(date1_1).isEqualTo(LocalDateTime.of(2025, 12, 10, 0, 0, 0))
        assertThat(date1_2).isEqualTo(LocalDateTime.of(2025, 11, 1, 0, 0, 0))
    }
}