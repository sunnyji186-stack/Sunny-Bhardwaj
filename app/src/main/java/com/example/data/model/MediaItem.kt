package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class OttPlatform(
    val displayName: String,
    val primaryColor: Color,
    val onColor: Color,
    val logoText: String,
    val subscriptionFeeSavedMonthly: Double
) {
    ALL("All OTTs", Color(0xFFE0E0E0), Color(0xFF121212), "ALL", 0.0),
    NETFLIX("Netflix", Color(0xFFE50914), Color.White, "N", 15.49),
    PRIME("Prime Video", Color(0xFF00A8E1), Color.White, "prime", 14.99),
    JIO_CINEMA("JioCinema", Color(0xFFD11A7A), Color.White, "Jio", 4.99),
    HOTSTAR("Hotstar", Color(0xFF01142F), Color(0xFF00E5FF), "hotstar", 9.99),
    ZEE5("Zee5", Color(0xFF820054), Color.White, "ZEE5", 5.99)
}

data class MediaItem(
    val id: String,
    val title: String,
    val platform: OttPlatform,
    val category: String,
    val description: String,
    val duration: String,
    val rating: String,
    val releaseYear: String,
    val isTrending: Boolean = false,
    val isPopular: Boolean = false,
    val gradientColors: List<Color>
)

object MediaDataProvider {
    val items = listOf(
        // NETFLIX
        MediaItem(
            id = "nf_1",
            title = "Squid Game: The Trial",
            platform = OttPlatform.NETFLIX,
            category = "Thriller",
            description = "Hundreds of cash-strapped players accept a strange invitation to compete in children's games. Inside, a tempting prize awaits with deadly high stakes.",
            duration = "1 Season",
            rating = "95% Match",
            releaseYear = "2024",
            isTrending = true,
            gradientColors = listOf(Color(0xFFE50914), Color(0xFF1E0304))
        ),
        MediaItem(
            id = "nf_2",
            title = "Stranger Dimensions",
            platform = OttPlatform.NETFLIX,
            category = "Sci-Fi",
            description = "When a young boy vanishes, a small town uncovers a mystery involving secret experiments, terrifying supernatural forces and one strange little girl.",
            duration = "4 Seasons",
            rating = "IMDb 8.7",
            releaseYear = "2022",
            isPopular = true,
            gradientColors = listOf(Color(0xFF8A2BE2), Color(0xFF0B0114))
        ),
        MediaItem(
            id = "nf_3",
            title = "Neon Heist",
            platform = OttPlatform.NETFLIX,
            category = "Action",
            description = "An unusual group of robbers attempt to carry out the most perfect heist in Spanish history - stealing 2.4 billion euros from the Royal Mint.",
            duration = "5 Seasons",
            rating = "91% Match",
            releaseYear = "2021",
            gradientColors = listOf(Color(0xFFFF4500), Color(0xFF1B0700))
        ),
        
        // PRIME VIDEO
        MediaItem(
            id = "pv_1",
            title = "The Sentinels",
            platform = OttPlatform.PRIME,
            category = "Action",
            description = "In a world where superheroes embrace the darker side of their massive celebrity, an elite group of vigilantes attempts to bring down corrupt corporatized heroes.",
            duration = "4 Seasons",
            rating = "IMDb 8.7",
            releaseYear = "2024",
            isTrending = true,
            gradientColors = listOf(Color(0xFF00A8E1), Color(0xFF001F2D))
        ),
        MediaItem(
            id = "pv_2",
            title = "Fallout Syndicate",
            platform = OttPlatform.PRIME,
            category = "Sci-Fi",
            description = "The future came early. Citizens of a luxury luxury shelter are forced to surface back to the irradiated hellscape their ancestors left behind.",
            duration = "1 Season",
            rating = "94% Match",
            releaseYear = "2024",
            isPopular = true,
            gradientColors = listOf(Color(0xFF8B8000), Color(0xFF1C1A00))
        ),
        MediaItem(
            id = "pv_3",
            title = "Citadel Rogue",
            platform = OttPlatform.PRIME,
            category = "Suspense",
            description = "Global spy agency Citadel has fallen. Years later, two elite syndicate agents have their memories wiped until a sudden rogue asset seeks their help to stop a syndicate world takeover.",
            duration = "2h 10m",
            rating = "IMDb 7.2",
            releaseYear = "2023",
            gradientColors = listOf(Color(0xFF4682B4), Color(0xFF0D171F))
        ),

        // JIO CINEMA
        MediaItem(
            id = "jc_1",
            title = "Asur: Rise of Darkness",
            platform = OttPlatform.JIO_CINEMA,
            category = "Crime Drama",
            description = "A unique psychological thriller that pits forensic science against the deep, ancient mysticism of Indian mythology in a terrifying chase of cat and mouse.",
            duration = "2 Seasons",
            rating = "IMDb 8.9",
            releaseYear = "2023",
            isTrending = true,
            gradientColors = listOf(Color(0xFFC71585), Color(0xFF2E001A))
        ),
        MediaItem(
            id = "jc_2",
            title = "Lanka: City of Gold",
            platform = OttPlatform.JIO_CINEMA,
            category = "Mythological Thriller",
            description = "A modern-day archaeologist stumbles upon ancient tablets in the ruins of Sri Lanka, awakening an ancient force that can control human minds.",
            duration = "1 Season",
            rating = "IMDb 8.1",
            releaseYear = "2025",
            isPopular = true,
            gradientColors = listOf(Color(0xFFFF1493), Color(0xFF260014))
        ),

        // DISNEY+ HOTSTAR
        MediaItem(
            id = "hs_1",
            title = "The Shogun Chronicles",
            platform = OttPlatform.HOTSTAR,
            category = "Historical Drama",
            description = "In Japan in the year 1600, Lord Yoshii Toranaga is fighting for his life as his enemies on the Council of Regents unite against him. A massive masterpiece.",
            duration = "1 Season",
            rating = "IMDb 9.1",
            releaseYear = "2024",
            isTrending = true,
            isPopular = true,
            gradientColors = listOf(Color(0xFF00E5FF), Color(0xFF001A20))
        ),
        MediaItem(
            id = "hs_2",
            title = "Cosmic Marvels",
            platform = OttPlatform.HOTSTAR,
            category = "Sci-Fi",
            description = "Journey through deep galaxies, temporal anomalies, and multiverse conflicts alongside the gods of mischief and quantum explorers of tomorrow.",
            duration = "2 Seasons",
            rating = "89% Match",
            releaseYear = "2023",
            gradientColors = listOf(Color(0xFF20B2AA), Color(0xFF052120))
        ),

        // ZEE5
        MediaItem(
            id = "z5_1",
            title = "Sunflower Secrets",
            platform = OttPlatform.ZEE5,
            category = "Dark Comedy",
            description = "When a resident of Sunflower cooperative housing society is found dead under mysterious circumstances, a quirky resident becomes key suspect.",
            duration = "2 Seasons",
            rating = "IMDb 8.0",
            releaseYear = "2024",
            isTrending = true,
            gradientColors = listOf(Color(0xFFDA70D6), Color(0xFF1E041E))
        ),
        MediaItem(
            id = "z5_2",
            title = "Taj: Empire of Blood",
            platform = OttPlatform.ZEE5,
            category = "Historical Action",
            description = "The bloody race to succeed the Mughal emperor Akbar begins. Brothers fight brothers, and empire stability hangs on a fine silken thread.",
            duration = "2 Seasons",
            rating = "84% Match",
            releaseYear = "2023",
            isPopular = true,
            gradientColors = listOf(Color(0xFFFF8C00), Color(0xFF221100))
        )
    )
}
