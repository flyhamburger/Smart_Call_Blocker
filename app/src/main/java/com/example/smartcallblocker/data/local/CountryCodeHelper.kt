package com.example.smartcallblocker.data.local

import com.example.smartcallblocker.data.local.entity.CountryCodeRuleEntity

data class CountryInfo(
    val name: String,
    val code: String, // e.g. "+1", "+44", "+91"
    val isoCode: String, // e.g. "US", "GB", "IN"
    val flagEmoji: String = getFlagEmoji(isoCode),
)

object CountryCodeHelper {

    val standardCountries: List<CountryInfo> = listOf(
        CountryInfo("United States", "+1", "US"),
        CountryInfo("Canada", "+1", "CA"),
        CountryInfo("United Kingdom", "+44", "GB"),
        CountryInfo("India", "+91", "IN"),
        CountryInfo("Germany", "+49", "DE"),
        CountryInfo("Japan", "+81", "JP"),
        CountryInfo("Australia", "+61", "AU"),
        CountryInfo("France", "+33", "FR"),
        CountryInfo("China", "+86", "CN"),
        CountryInfo("Brazil", "+55", "BR"),
        CountryInfo("Mexico", "+52", "MX"),
        CountryInfo("Italy", "+39", "IT"),
        CountryInfo("Spain", "+34", "ES"),
        CountryInfo("South Korea", "+82", "KR"),
        CountryInfo("Russia", "+7", "RU"),
        CountryInfo("Nigeria", "+234", "NG"),
        CountryInfo("South Africa", "+27", "ZA"),
        CountryInfo("Egypt", "+20", "EG"),
        CountryInfo("United Arab Emirates", "+971", "AE"),
        CountryInfo("Saudi Arabia", "+966", "SA"),
        CountryInfo("Singapore", "+65", "SG"),
        CountryInfo("Pakistan", "+92", "PK"),
        CountryInfo("Bangladesh", "+880", "BD"),
        CountryInfo("Indonesia", "+62", "ID"),
        CountryInfo("Philippines", "+63", "PH"),
        CountryInfo("Vietnam", "+84", "VN"),
        CountryInfo("Turkey", "+90", "TR"),
        CountryInfo("Argentina", "+54", "AR"),
        CountryInfo("Colombia", "+57", "CO"),
        CountryInfo("Netherlands", "+31", "NL"),
        CountryInfo("Sweden", "+46", "SE"),
        CountryInfo("Switzerland", "+41", "CH"),
        CountryInfo("Poland", "+48", "PL"),
        CountryInfo("Belgium", "+32", "BE"),
        CountryInfo("Austria", "+43", "AT"),
        CountryInfo("New Zealand", "+64", "NZ"),
        CountryInfo("Ireland", "+353", "IE"),
        CountryInfo("Thailand", "+66", "TH"),
        CountryInfo("Malaysia", "+60", "MY"),
        CountryInfo("Ukraine", "+380", "UA"),
        CountryInfo("Greece", "+30", "GR"),
        CountryInfo("Portugal", "+351", "PT"),
        CountryInfo("Israel", "+972", "IL"),
        CountryInfo("Hong Kong", "+852", "HK"),
        CountryInfo("Taiwan", "+886", "TW"),
        CountryInfo("Kenya", "+254", "KE"),
        CountryInfo("Ghana", "+233", "GH"),
        CountryInfo("Morocco", "+212", "MA"),
        CountryInfo("Chile", "+56", "CL"),
        CountryInfo("Peru", "+51", "PE"),
        CountryInfo("Czech Republic", "+420", "CZ"),
        CountryInfo("Romania", "+40", "RO"),
        CountryInfo("Hungary", "+36", "HU"),
        CountryInfo("Denmark", "+45", "DK"),
        CountryInfo("Norway", "+47", "NO"),
        CountryInfo("Finland", "+358", "FI")
    )

    fun searchCountries(query: String): List<CountryInfo> {
        if (query.isBlank()) return standardCountries
        val trimmed = query.trim().lowercase()
        return standardCountries.filter { country ->
            country.name.lowercase().contains(trimmed) ||
                    country.code.contains(trimmed) ||
                    country.isoCode.lowercase().contains(trimmed)
        }
    }

    fun findByCode(code: String): List<CountryInfo> {
        val cleanCode = if (!code.startsWith("+")) "+$code" else code
        return standardCountries.filter { it.code == cleanCode }
    }

    fun findByIso(isoCode: String): CountryInfo? {
        return standardCountries.firstOrNull { it.isoCode.equals(isoCode, ignoreCase = true) }
    }

    fun CountryInfo.toRuleEntity(isEnabled: Boolean = true): CountryCodeRuleEntity {
        return CountryCodeRuleEntity(
            countryCode = this.code,
            countryName = this.name,
            isEnabled = isEnabled
        )
    }
}

fun getFlagEmoji(isoCode: String): String {
    if (isoCode.length != 2) return ""
    val upperIso = isoCode.uppercase()
    val firstChar = (Character.codePointAt(upperIso, 0) - 0x41) + 0x1F1E6
    val secondChar = (Character.codePointAt(upperIso, 1) - 0x41) + 0x1F1E6
    return String(Character.toChars(firstChar)) + String(Character.toChars(secondChar))
}
