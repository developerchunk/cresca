package com.developerstring.nexpay

import androidx.compose.ui.graphics.Color

data class Country(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val color: Color = Color.Green,
    val borderCoordinates: List<Pair<Double, Double>> = emptyList()
)

data object Constants {
    const val DATABASE_NAME = "app_database.db"
    const val DATABASE_VERSION = 1


    val CountriesWithCoordinates = listOf(
        // North America (4)
        Country(
            "Canada", 56.1304, -106.3468, Color(0xFFFF5722),
            listOf(
                83.0 to -70.0,
                80.0 to -90.0,
                75.0 to -110.0,
                70.0 to -140.0,
                70.0 to -95.0,
                70.0 to -60.0,
                60.0 to -64.0,
                52.0 to -55.0,
                47.0 to -60.0,
                45.0 to -75.0,
                49.0 to -95.0,
                49.0 to -123.0,
                60.0 to -135.0,
                70.0 to -140.0,
                83.0 to -70.0
            )
        ),
        Country(
            "Greenland", 71.7069, -42.6043, Color(0xFF81C784),
            listOf(
                83.5 to -32.0,
                83.0 to -35.0,
                81.0 to -55.0,
                78.0 to -72.0,
                76.0 to -68.0,
                75.0 to -58.0,
                72.0 to -55.0,
                70.0 to -51.0,
                69.0 to -50.0,
                68.0 to -52.0,
                66.0 to -53.0,
                64.0 to -50.0,
                60.0 to -44.0,
                60.5 to -43.0,
                63.0 to -41.0,
                65.0 to -37.0,
                68.0 to -31.0,
                70.0 to -28.0,
                72.0 to -24.0,
                75.0 to -20.0,
                78.0 to -18.0,
                81.0 to -15.0,
                83.5 to -32.0
            )
        ),
        Country(
            "USA", 37.0902, -95.7129, Color(0xFF4CAF50),
            listOf(
                49.0 to -125.0,
                49.0 to -95.0,
                49.0 to -67.0,
                45.0 to -67.0,
                40.0 to -74.0,
                35.0 to -75.0,
                30.0 to -81.0,
                25.0 to -80.0,
                26.0 to -97.0,
                28.0 to -97.0,
                32.0 to -117.0,
                42.0 to -124.0,
                49.0 to -125.0
            )
        ),
        Country(
            "Mexico", 23.6345, -102.5528, Color(0xFF607D8B),
            listOf(
                32.0 to -117.0,
                32.0 to -106.0,
                31.0 to -103.0,
                26.0 to -97.0,
                22.0 to -97.0,
                18.0 to -94.0,
                16.0 to -92.0,
                15.0 to -92.0,
                17.0 to -101.0,
                21.0 to -105.0,
                28.0 to -112.0,
                32.0 to -117.0
            )
        ),

        // Central America & Caribbean (20)
        Country(
            "Antigua and Barbuda", 17.0608, -61.7964, Color(0xFFE91E63),
            listOf(17.5 to -61.9, 17.5 to -61.7, 17.0 to -61.7, 17.0 to -61.9, 17.5 to -61.9)
        ),
        Country(
            "Bahamas", 25.0343, -77.3963, Color(0xFF00BCD4),
            listOf(
                27.0 to -78.5,
                27.0 to -77.0,
                24.0 to -74.5,
                22.0 to -74.0,
                21.0 to -77.0,
                23.0 to -78.0,
                27.0 to -78.5
            )
        ),
        Country(
            "Barbados", 13.1939, -59.5432, Color(0xFF3F51B5),
            listOf(13.4 to -59.7, 13.4 to -59.4, 13.0 to -59.4, 13.0 to -59.7, 13.4 to -59.7)
        ),
        Country(
            "Belize", 17.1899, -88.4976, Color(0xFF4CAF50),
            listOf(18.5 to -88.3, 18.5 to -87.8, 16.0 to -88.9, 15.9 to -89.2, 17.8 to -89.1, 18.5 to -88.3)
        ),
        Country(
            "Costa Rica", 9.7489, -83.7534, Color(0xFFFF5722),
            listOf(11.2 to -85.9, 11.0 to -82.5, 9.0 to -82.5, 8.0 to -83.0, 8.5 to -85.0, 10.5 to -85.9, 11.2 to -85.9)
        ),
        Country(
            "Cuba", 21.5218, -77.7812, Color(0xFF26A69A),
            listOf(23.0 to -85.0, 23.0 to -74.0, 20.0 to -74.0, 20.0 to -85.0, 23.0 to -85.0)
        ),
        Country(
            "Dominica", 15.4150, -61.3710, Color(0xFF9C27B0),
            listOf(15.7 to -61.5, 15.7 to -61.2, 15.2 to -61.2, 15.2 to -61.5, 15.7 to -61.5)
        ),
        Country(
            "Dominican Republic", 18.7357, -70.1627, Color(0xFFFF9800),
            listOf(20.0 to -72.0, 20.0 to -68.3, 17.5 to -68.3, 17.5 to -71.7, 19.0 to -72.0, 20.0 to -72.0)
        ),
        Country(
            "El Salvador", 13.7942, -88.8965, Color(0xFF8BC34A),
            listOf(14.5 to -90.1, 14.5 to -87.7, 13.2 to -87.7, 13.2 to -90.1, 14.5 to -90.1)
        ),
        Country(
            "Grenada", 12.1165, -61.6790, Color(0xFFE91E63),
            listOf(12.3 to -61.8, 12.3 to -61.5, 12.0 to -61.5, 12.0 to -61.8, 12.3 to -61.8)
        ),
        Country(
            "Guatemala", 15.7835, -90.2308, Color(0xFF8D6E63),
            listOf(15.0 to -92.0, 17.0 to -92.0, 18.0 to -88.0, 14.0 to -88.0, 14.0 to -92.0, 15.0 to -92.0)
        ),
        Country(
            "Haiti", 18.9712, -72.2852, Color(0xFFFF5722),
            listOf(20.0 to -74.5, 20.0 to -71.6, 18.0 to -71.6, 18.0 to -74.5, 19.0 to -74.5, 20.0 to -74.5)
        ),
        Country(
            "Honduras", 15.2000, -86.2419, Color(0xFF00BCD4),
            listOf(16.5 to -89.3, 16.5 to -83.1, 13.0 to -83.1, 13.0 to -89.3, 16.5 to -89.3)
        ),
        Country(
            "Jamaica", 18.1096, -77.2975, Color(0xFFFFEB3B),
            listOf(18.5 to -78.4, 18.5 to -76.2, 17.7 to -76.2, 17.7 to -78.4, 18.5 to -78.4)
        ),
        Country(
            "Nicaragua", 12.8654, -85.2072, Color(0xFF9C27B0),
            listOf(15.0 to -87.7, 15.0 to -83.0, 10.7 to -83.0, 10.7 to -87.7, 15.0 to -87.7)
        ),
        Country(
            "Panama", 8.5380, -80.7821, Color(0xFF2196F3),
            listOf(9.7 to -83.0, 9.7 to -77.2, 7.2 to -77.2, 7.2 to -79.5, 8.0 to -82.9, 9.7 to -83.0)
        ),
        Country(
            "Saint Kitts and Nevis", 17.3578, -62.7830, Color(0xFFE91E63),
            listOf(17.5 to -62.9, 17.5 to -62.5, 17.2 to -62.5, 17.2 to -62.9, 17.5 to -62.9)
        ),
        Country(
            "Saint Lucia", 13.9094, -60.9789, Color(0xFF00BCD4),
            listOf(14.2 to -61.1, 14.2 to -60.9, 13.7 to -60.9, 13.7 to -61.1, 14.2 to -61.1)
        ),
        Country(
            "Saint Vincent and the Grenadines", 12.9843, -61.2872, Color(0xFF4CAF50),
            listOf(13.4 to -61.5, 13.4 to -61.1, 12.5 to -61.1, 12.5 to -61.5, 13.4 to -61.5)
        ),
        Country(
            "Trinidad and Tobago", 10.6918, -61.2225, Color(0xFFFF9800),
            listOf(11.4 to -61.9, 11.4 to -60.5, 10.0 to -60.5, 10.0 to -61.9, 11.4 to -61.9)
        ),

        // South America (12)
        Country(
            "Argentina", -38.4161, -63.6167, Color(0xFF009688),
            listOf(
                -22.0 to -62.0,
                -27.0 to -58.0,
                -34.0 to -58.0,
                -40.0 to -62.0,
                -50.0 to -69.0,
                -52.0 to -70.0,
                -52.0 to -68.0,
                -42.0 to -65.0,
                -32.0 to -68.0,
                -23.0 to -65.0,
                -22.0 to -62.0
            )
        ),
        Country(
            "Bolivia", -16.2902, -63.5887, Color(0xFFFF5722),
            listOf(
                -10.0 to -69.0,
                -10.0 to -58.0,
                -17.0 to -57.5,
                -22.0 to -62.0,
                -22.5 to -69.5,
                -18.0 to -69.0,
                -10.0 to -69.0
            )
        ),
        Country(
            "Brazil", -14.2350, -51.9253, Color(0xFFFFEB3B),
            listOf(
                5.0 to -60.0,
                2.0 to -51.0,
                -5.0 to -35.0,
                -15.0 to -38.0,
                -23.0 to -43.0,
                -28.0 to -48.0,
                -33.0 to -53.0,
                -30.0 to -57.0,
                -22.0 to -57.0,
                -16.0 to -58.0,
                -10.0 to -68.0,
                -5.0 to -73.0,
                0.0 to -70.0,
                5.0 to -60.0
            )
        ),
        Country(
            "Chile", -35.6751, -71.5430, Color(0xFF5C6BC0),
            listOf(
                -18.0 to -70.0,
                -30.0 to -71.0,
                -40.0 to -73.0,
                -50.0 to -74.0,
                -53.0 to -71.0,
                -52.0 to -68.0,
                -42.0 to -65.0,
                -32.0 to -68.0,
                -23.0 to -65.0,
                -18.0 to -70.0
            )
        ),
        Country(
            "Colombia", 4.5709, -74.2973, Color(0xFFFFA726),
            listOf(
                12.0 to -72.0,
                10.0 to -67.0,
                4.0 to -67.0,
                0.0 to -75.0,
                2.0 to -79.0,
                8.0 to -77.0,
                11.0 to -75.0,
                12.0 to -72.0
            )
        ),
        Country(
            "Ecuador", -1.8312, -78.1834, Color(0xFF26A69A),
            listOf(2.0 to -79.0, 1.0 to -75.5, -5.0 to -75.0, -5.0 to -81.0, 0.0 to -80.0, 2.0 to -79.0)
        ),
        Country(
            "Guyana", 4.8604, -58.9302, Color(0xFFFF9800),
            listOf(8.5 to -60.0, 8.0 to -57.0, 1.2 to -56.5, 2.0 to -59.8, 5.0 to -61.0, 8.5 to -60.0)
        ),
        Country(
            "Paraguay", -23.4425, -58.4438, Color(0xFF2196F3),
            listOf(-19.3 to -62.0, -19.3 to -54.6, -27.5 to -54.6, -27.5 to -62.0, -19.3 to -62.0)
        ),
        Country(
            "Peru", -9.1900, -75.0152, Color(0xFFEC407A),
            listOf(
                0.0 to -75.0,
                -4.0 to -81.0,
                -10.0 to -80.0,
                -14.0 to -76.0,
                -18.0 to -70.0,
                -15.0 to -69.0,
                -10.0 to -68.0,
                -5.0 to -73.0,
                0.0 to -75.0
            )
        ),
        Country(
            "Suriname", 3.9193, -56.0278, Color(0xFF8BC34A),
            listOf(6.0 to -58.0, 6.0 to -54.0, 2.0 to -54.0, 2.0 to -58.0, 6.0 to -58.0)
        ),
        Country(
            "Uruguay", -32.5228, -55.7658, Color(0xFF00BCD4),
            listOf(-30.0 to -57.6, -30.0 to -53.1, -35.0 to -53.1, -35.0 to -57.6, -30.0 to -57.6)
        ),
        Country(
            "Venezuela", 6.4238, -66.5897, Color(0xFFAB47BC),
            listOf(12.0 to -72.0, 11.0 to -60.0, 8.0 to -60.0, 5.0 to -67.0, 10.0 to -73.0, 12.0 to -72.0)
        ),

        // Europe (44)
        Country(
            "Albania", 41.1533, 20.1683, Color(0xFFE91E63),
            listOf(42.5 to 19.3, 42.5 to 21.0, 39.6 to 20.0, 39.6 to 19.3, 42.5 to 19.3)
        ),
        Country(
            "Andorra", 42.5063, 1.5218, Color(0xFF9C27B0),
            listOf(42.7 to 1.4, 42.7 to 1.8, 42.4 to 1.8, 42.4 to 1.4, 42.7 to 1.4)
        ),
        Country(
            "Austria", 47.5162, 14.5501, Color(0xFFFF5722),
            listOf(49.0 to 9.5, 49.0 to 17.2, 46.4 to 17.2, 46.4 to 9.5, 49.0 to 9.5)
        ),
        Country(
            "Belarus", 53.7098, 27.9534, Color(0xFF00BCD4),
            listOf(56.2 to 23.2, 56.2 to 32.8, 51.3 to 32.8, 51.3 to 23.2, 56.2 to 23.2)
        ),
        Country(
            "Belgium", 50.5039, 4.4699, Color(0xFF8BC34A),
            listOf(51.5 to 2.5, 51.5 to 6.4, 49.5 to 6.4, 49.5 to 2.5, 51.5 to 2.5)
        ),
        Country(
            "Bosnia and Herzegovina", 43.9159, 17.6791, Color(0xFFFF9800),
            listOf(45.3 to 15.7, 45.3 to 19.6, 42.6 to 19.6, 42.6 to 15.7, 45.3 to 15.7)
        ),
        Country(
            "Bulgaria", 42.7339, 25.4858, Color(0xFF2196F3),
            listOf(44.2 to 22.4, 44.2 to 28.6, 41.2 to 28.6, 41.2 to 22.4, 44.2 to 22.4)
        ),
        Country(
            "Croatia", 45.1, 15.2, Color(0xFFE91E63),
            listOf(46.5 to 13.5, 46.5 to 19.4, 42.4 to 19.4, 42.4 to 13.5, 46.5 to 13.5)
        ),
        Country(
            "Cyprus", 35.1264, 33.4299, Color(0xFF4CAF50),
            listOf(35.7 to 32.3, 35.7 to 34.6, 34.6 to 34.6, 34.6 to 32.3, 35.7 to 32.3)
        ),
        Country(
            "Czech Republic", 49.8175, 15.4730, Color(0xFF9C27B0),
            listOf(51.1 to 12.1, 51.1 to 18.9, 48.6 to 18.9, 48.6 to 12.1, 51.1 to 12.1)
        ),
        Country(
            "Denmark", 56.2639, 9.5018, Color(0xFFFF5722),
            listOf(57.8 to 8.1, 57.8 to 15.2, 54.6 to 15.2, 54.6 to 8.1, 57.8 to 8.1)
        ),
        Country(
            "Estonia", 58.5953, 25.0136, Color(0xFF00BCD4),
            listOf(59.7 to 21.8, 59.7 to 28.2, 57.5 to 28.2, 57.5 to 21.8, 59.7 to 21.8)
        ),
        Country(
            "Finland", 61.9241, 25.7482, Color(0xFFFFEB3B),
            listOf(70.1 to 20.5, 70.1 to 31.6, 59.8 to 31.6, 59.8 to 20.5, 70.1 to 20.5)
        ),
        Country(
            "France", 46.2276, 2.2137, Color(0xFF3F51B5),
            listOf(
                51.0 to 2.5,
                49.0 to 8.0,
                48.0 to 7.5,
                43.0 to 6.0,
                42.5 to 3.0,
                43.0 to -1.0,
                47.0 to -5.0,
                49.0 to -2.0,
                51.0 to 2.5
            )
        ),
        Country(
            "Germany", 51.1657, 10.4515, Color(0xFF8BC34A),
            listOf(
                55.0 to 9.0,
                54.0 to 14.0,
                51.0 to 15.0,
                48.0 to 13.0,
                47.0 to 10.0,
                47.0 to 7.0,
                51.0 to 6.0,
                54.0 to 7.0,
                55.0 to 9.0
            )
        ),
        Country(
            "Greece", 39.0742, 21.8243, Color(0xFF42A5F5),
            listOf(42.0 to 20.0, 42.0 to 28.0, 35.0 to 28.0, 35.0 to 20.0, 42.0 to 20.0)
        ),
        Country(
            "Hungary", 47.1625, 19.5033, Color(0xFFFF9800),
            listOf(48.6 to 16.1, 48.6 to 22.9, 45.7 to 22.9, 45.7 to 16.1, 48.6 to 16.1)
        ),
        Country(
            "Iceland", 64.9631, -19.0208, Color(0xFF00BCD4),
            listOf(66.5 to -24.5, 66.5 to -13.5, 63.4 to -13.5, 63.4 to -24.5, 66.5 to -24.5)
        ),
        Country(
            "Ireland", 53.4129, -8.2439, Color(0xFF4CAF50),
            listOf(55.4 to -10.5, 55.4 to -6.0, 51.4 to -6.0, 51.4 to -10.5, 55.4 to -10.5)
        ),
        Country(
            "Italy", 41.8719, 12.5674, Color(0xFF66BB6A),
            listOf(
                47.0 to 12.0,
                45.0 to 13.0,
                40.0 to 18.0,
                38.0 to 16.0,
                37.0 to 15.0,
                40.0 to 9.0,
                44.0 to 7.0,
                47.0 to 12.0
            )
        ),
        Country(
            "Kosovo", 42.6026, 20.9030, Color(0xFFE91E63),
            listOf(43.2 to 20.0, 43.2 to 21.8, 42.0 to 21.8, 42.0 to 20.0, 43.2 to 20.0)
        ),
        Country(
            "Latvia", 56.8796, 24.6032, Color(0xFF9C27B0),
            listOf(58.1 to 21.0, 58.1 to 28.2, 55.7 to 28.2, 55.7 to 21.0, 58.1 to 21.0)
        ),
        Country(
            "Liechtenstein", 47.1660, 9.5554, Color(0xFFFF5722),
            listOf(47.3 to 9.5, 47.3 to 9.6, 47.0 to 9.6, 47.0 to 9.5, 47.3 to 9.5)
        ),
        Country(
            "Lithuania", 55.1694, 23.8813, Color(0xFF00BCD4),
            listOf(56.5 to 21.0, 56.5 to 26.8, 53.9 to 26.8, 53.9 to 21.0, 56.5 to 21.0)
        ),
        Country(
            "Luxembourg", 49.8153, 6.1296, Color(0xFF8BC34A),
            listOf(50.2 to 5.7, 50.2 to 6.5, 49.4 to 6.5, 49.4 to 5.7, 50.2 to 5.7)
        ),
        Country(
            "Malta", 35.9375, 14.3754, Color(0xFFFF9800),
            listOf(36.1 to 14.2, 36.1 to 14.6, 35.8 to 14.6, 35.8 to 14.2, 36.1 to 14.2)
        ),
        Country(
            "Moldova", 47.4116, 28.3699, Color(0xFF2196F3),
            listOf(48.5 to 26.6, 48.5 to 30.1, 45.5 to 30.1, 45.5 to 26.6, 48.5 to 26.6)
        ),
        Country(
            "Monaco", 43.7384, 7.4246, Color(0xFFE91E63),
            listOf(43.8 to 7.4, 43.8 to 7.5, 43.7 to 7.5, 43.7 to 7.4, 43.8 to 7.4)
        ),
        Country(
            "Montenegro", 42.7087, 19.3744, Color(0xFF4CAF50),
            listOf(43.6 to 18.4, 43.6 to 20.4, 41.9 to 20.4, 41.9 to 18.4, 43.6 to 18.4)
        ),
        Country(
            "Netherlands", 52.1326, 5.2913, Color(0xFF9C27B0),
            listOf(53.5 to 3.4, 53.5 to 7.2, 50.8 to 7.2, 50.8 to 3.4, 53.5 to 3.4)
        ),
        Country(
            "North Macedonia", 41.6086, 21.7453, Color(0xFFFF5722),
            listOf(42.4 to 20.5, 42.4 to 23.0, 40.9 to 23.0, 40.9 to 20.5, 42.4 to 20.5)
        ),
        Country(
            "Norway", 60.4720, 8.4689, Color(0xFF26C6DA),
            listOf(71.0 to 25.0, 70.0 to 30.0, 58.0 to 5.0, 60.0 to 5.0, 71.0 to 25.0)
        ),
        Country(
            "Poland", 51.9194, 19.1451, Color(0xFFAB47BC),
            listOf(54.0 to 14.0, 54.0 to 24.0, 49.0 to 24.0, 49.0 to 14.0, 54.0 to 14.0)
        ),
        Country(
            "Portugal", 39.3999, -8.2245, Color(0xFF00BCD4),
            listOf(42.2 to -8.9, 42.2 to -6.2, 37.0 to -6.2, 37.0 to -9.5, 42.2 to -8.9)
        ),
        Country(
            "Romania", 45.9432, 24.9668, Color(0xFF8BC34A),
            listOf(48.3 to 20.3, 48.3 to 29.7, 43.6 to 29.7, 43.6 to 20.3, 48.3 to 20.3)
        ),
        Country(
            "Russia", 61.5240, 105.3188, Color(0xFF00BCD4),
            listOf(
                81.0 to 30.0,
                80.0 to 50.0,
                78.0 to 70.0,
                75.0 to 90.0,
                74.0 to 110.0,
                75.0 to 130.0,
                73.0 to 150.0,
                71.0 to 170.0,
                69.0 to -178.0,
                66.0 to -168.0,
                64.0 to -165.0,
                62.0 to 175.0,
                60.0 to 163.0,
                56.0 to 160.0,
                53.0 to 156.0,
                50.0 to 155.0,
                48.0 to 142.0,
                46.5 to 140.0,
                45.0 to 134.0,
                43.5 to 131.0,
                42.5 to 128.0,
                50.0 to 117.0,
                50.5 to 106.0,
                51.0 to 95.0,
                52.0 to 87.0,
                54.0 to 78.0,
                55.0 to 68.0,
                55.0 to 61.0,
                54.0 to 55.0,
                52.0 to 50.0,
                50.0 to 46.0,
                47.0 to 42.0,
                45.0 to 39.0,
                46.0 to 37.0,
                48.0 to 36.0,
                51.0 to 34.0,
                52.0 to 31.0,
                54.0 to 28.0,
                56.0 to 27.0,
                58.0 to 27.0,
                60.0 to 28.0,
                63.0 to 30.0,
                66.0 to 32.0,
                68.0 to 33.0,
                69.0 to 31.0,
                70.0 to 30.0,
                73.0 to 31.0,
                77.0 to 32.0,
                81.0 to 30.0
            )
        ),
        Country(
            "San Marino", 43.9424, 12.4578, Color(0xFFFF9800),
            listOf(44.0 to 12.4, 44.0 to 12.5, 43.9 to 12.5, 43.9 to 12.4, 44.0 to 12.4)
        ),
        Country(
            "Serbia", 44.0165, 21.0059, Color(0xFF2196F3),
            listOf(46.2 to 18.8, 46.2 to 23.0, 42.2 to 23.0, 42.2 to 18.8, 46.2 to 18.8)
        ),
        Country(
            "Slovakia", 48.6690, 19.6990, Color(0xFFE91E63),
            listOf(49.6 to 16.8, 49.6 to 22.6, 47.7 to 22.6, 47.7 to 16.8, 49.6 to 16.8)
        ),
        Country(
            "Slovenia", 46.1512, 14.9955, Color(0xFF4CAF50),
            listOf(46.9 to 13.4, 46.9 to 16.6, 45.4 to 16.6, 45.4 to 13.4, 46.9 to 13.4)
        ),
        Country(
            "Spain", 40.4637, -3.7492, Color(0xFFF4511E),
            listOf(43.0 to -8.0, 43.0 to 3.0, 38.0 to 0.0, 36.0 to -6.0, 37.0 to -7.0, 43.0 to -8.0)
        ),
        Country(
            "Sweden", 60.1282, 18.6435, Color(0xFF29B6F6),
            listOf(69.0 to 20.0, 69.0 to 24.0, 55.0 to 14.0, 55.0 to 11.0, 69.0 to 20.0)
        ),
        Country(
            "Switzerland", 46.8182, 8.2275, Color(0xFF9C27B0),
            listOf(47.8 to 6.0, 47.8 to 10.5, 45.8 to 10.5, 45.8 to 6.0, 47.8 to 6.0)
        ),
        Country(
            "Ukraine", 48.3794, 31.1656, Color(0xFFFFCA28),
            listOf(52.0 to 24.0, 52.0 to 40.0, 45.0 to 40.0, 45.0 to 22.0, 52.0 to 24.0)
        ),
        Country(
            "United Kingdom", 55.3781, -3.4360, Color(0xFF2196F3),
            listOf(59.0 to -3.0, 58.0 to 0.0, 52.0 to 2.0, 50.0 to -5.0, 55.0 to -7.0, 59.0 to -3.0)
        ),
        Country(
            "Vatican City", 41.9029, 12.4534, Color(0xFFFFEB3B),
            listOf(41.91 to 12.45, 41.91 to 12.46, 41.90 to 12.46, 41.90 to 12.45, 41.91 to 12.45)
        ),

        // Africa (54)
        Country(
            "Algeria", 28.0339, 1.6596, Color(0xFFFF7043),
            listOf(37.0 to -8.0, 37.0 to 9.0, 19.0 to 9.0, 19.0 to -8.0, 37.0 to -8.0)
        ),
        Country(
            "Angola", -11.2027, 17.8739, Color(0xFFFFB300),
            listOf(-4.0 to 12.0, -4.0 to 24.0, -18.0 to 24.0, -18.0 to 12.0, -4.0 to 12.0)
        ),
        Country(
            "Benin", 9.3077, 2.3158, Color(0xFF4CAF50),
            listOf(12.4 to 0.8, 12.4 to 3.8, 6.2 to 3.8, 6.2 to 0.8, 12.4 to 0.8)
        ),
        Country(
            "Botswana", -22.3285, 24.6849, Color(0xFF9C27B0),
            listOf(-18.0 to 20.0, -18.0 to 29.0, -27.0 to 29.0, -27.0 to 20.0, -18.0 to 20.0)
        ),
        Country(
            "Burkina Faso", 12.2383, -1.5616, Color(0xFFFF5722),
            listOf(15.1 to -5.5, 15.1 to 2.4, 9.4 to 2.4, 9.4 to -5.5, 15.1 to -5.5)
        ),
        Country(
            "Burundi", -3.3731, 29.9189, Color(0xFF00BCD4),
            listOf(-2.3 to 29.0, -2.3 to 30.8, -4.5 to 30.8, -4.5 to 29.0, -2.3 to 29.0)
        ),
        Country(
            "Cabo Verde", 16.5388, -23.0418, Color(0xFF8BC34A),
            listOf(17.2 to -25.4, 17.2 to -22.7, 14.8 to -22.7, 14.8 to -25.4, 17.2 to -25.4)
        ),
        Country(
            "Cameroon", 7.3697, 12.3547, Color(0xFFFF9800),
            listOf(13.1 to 8.5, 13.1 to 16.2, 1.7 to 16.2, 1.7 to 8.5, 13.1 to 8.5)
        ),
        Country(
            "Central African Republic", 6.6111, 20.9394, Color(0xFF2196F3),
            listOf(11.0 to 14.4, 11.0 to 27.5, 2.2 to 27.5, 2.2 to 14.4, 11.0 to 14.4)
        ),
        Country(
            "Chad", 15.4542, 18.7322, Color(0xFFE91E63),
            listOf(23.5 to 14.0, 23.5 to 24.0, 7.4 to 24.0, 7.4 to 14.0, 23.5 to 14.0)
        ),
        Country(
            "Comoros", -11.6455, 43.3333, Color(0xFF4CAF50),
            listOf(-11.4 to 43.2, -11.4 to 43.5, -12.4 to 43.5, -12.4 to 43.2, -11.4 to 43.2)
        ),
        Country(
            "Congo", -4.0383, 21.7587, Color(0xFF7E57C2),
            listOf(4.0 to 12.0, 4.0 to 31.0, -13.0 to 31.0, -13.0 to 12.0, 4.0 to 12.0)
        ),
        Country(
            "Democratic Republic of the Congo", -4.0383, 21.7587, Color(0xFF9C27B0),
            listOf(5.4 to 12.2, 5.4 to 31.3, -13.5 to 31.3, -13.5 to 12.2, 5.4 to 12.2)
        ),
        Country(
            "Djibouti", 11.8251, 42.5903, Color(0xFFFF5722),
            listOf(12.7 to 41.8, 12.7 to 43.4, 10.9 to 43.4, 10.9 to 41.8, 12.7 to 41.8)
        ),
        Country(
            "Egypt", 26.8206, 30.8025, Color(0xFFFFC107),
            listOf(
                32.0 to 25.0,
                31.0 to 34.0,
                28.0 to 35.0,
                24.0 to 37.0,
                22.0 to 36.0,
                22.0 to 31.0,
                24.0 to 25.0,
                29.0 to 25.0,
                32.0 to 25.0
            )
        ),
        Country(
            "Equatorial Guinea", 1.6508, 10.2679, Color(0xFF00BCD4),
            listOf(2.3 to 9.3, 2.3 to 11.3, 0.9 to 11.3, 0.9 to 9.3, 2.3 to 9.3)
        ),
        Country(
            "Eritrea", 15.1794, 39.7823, Color(0xFF8BC34A),
            listOf(18.0 to 36.4, 18.0 to 43.1, 12.4 to 43.1, 12.4 to 36.4, 18.0 to 36.4)
        ),
        Country(
            "Eswatini", -26.5225, 31.4659, Color(0xFFFF9800),
            listOf(-25.7 to 30.8, -25.7 to 32.1, -27.3 to 32.1, -27.3 to 30.8, -25.7 to 30.8)
        ),
        Country(
            "Ethiopia", 9.1450, 40.4897, Color(0xFFEF5350),
            listOf(15.0 to 33.0, 15.0 to 48.0, 3.0 to 48.0, 3.0 to 33.0, 15.0 to 33.0)
        ),
        Country(
            "Gabon", -0.8037, 11.6094, Color(0xFF2196F3),
            listOf(2.3 to 8.7, 2.3 to 14.5, -4.0 to 14.5, -4.0 to 8.7, 2.3 to 8.7)
        ),
        Country(
            "Gambia", 13.4432, -15.3101, Color(0xFFE91E63),
            listOf(13.8 to -16.8, 13.8 to -13.8, 13.1 to -13.8, 13.1 to -16.8, 13.8 to -16.8)
        ),
        Country(
            "Ghana", 7.9465, -1.0232, Color(0xFF4CAF50),
            listOf(11.2 to -3.3, 11.2 to 1.2, 4.7 to 1.2, 4.7 to -3.3, 11.2 to -3.3)
        ),
        Country(
            "Guinea", 9.9456, -9.6966, Color(0xFF9C27B0),
            listOf(12.7 to -15.0, 12.7 to -7.7, 7.2 to -7.7, 7.2 to -15.0, 12.7 to -15.0)
        ),
        Country(
            "Guinea-Bissau", 11.8037, -15.1804, Color(0xFFFF5722),
            listOf(12.7 to -16.7, 12.7 to -13.6, 11.0 to -13.6, 11.0 to -16.7, 12.7 to -16.7)
        ),
        Country(
            "Ivory Coast", 7.5400, -5.5471, Color(0xFF00BCD4),
            listOf(10.7 to -8.6, 10.7 to -2.5, 4.4 to -2.5, 4.4 to -8.6, 10.7 to -8.6)
        ),
        Country(
            "Kenya", -0.0236, 37.9062, Color(0xFF26A69A),
            listOf(5.0 to 34.0, 5.0 to 42.0, -5.0 to 42.0, -5.0 to 34.0, 5.0 to 34.0)
        ),
        Country(
            "Lesotho", -29.6100, 28.2336, Color(0xFF8BC34A),
            listOf(-28.6 to 27.3, -28.6 to 29.5, -30.7 to 29.5, -30.7 to 27.3, -28.6 to 27.3)
        ),
        Country(
            "Liberia", 6.4281, -9.4295, Color(0xFFFF9800),
            listOf(8.6 to -11.5, 8.6 to -7.4, 4.4 to -7.4, 4.4 to -11.5, 8.6 to -11.5)
        ),
        Country(
            "Libya", 26.3351, 17.2283, Color(0xFFFFB74D),
            listOf(33.0 to 10.0, 33.0 to 25.0, 20.0 to 25.0, 20.0 to 10.0, 33.0 to 10.0)
        ),
        Country(
            "Madagascar", -18.7669, 46.8691, Color(0xFF2196F3),
            listOf(-12.0 to 43.2, -12.0 to 50.5, -25.6 to 50.5, -25.6 to 43.2, -12.0 to 43.2)
        ),
        Country(
            "Malawi", -13.2543, 34.3015, Color(0xFFE91E63),
            listOf(-9.4 to 33.0, -9.4 to 36.0, -17.1 to 36.0, -17.1 to 33.0, -9.4 to 33.0)
        ),
        Country(
            "Mali", 17.5707, -3.9962, Color(0xFF4CAF50),
            listOf(25.0 to -12.2, 25.0 to 4.3, 10.2 to 4.3, 10.2 to -12.2, 25.0 to -12.2)
        ),
        Country(
            "Mauritania", 21.0079, -10.9408, Color(0xFF9C27B0),
            listOf(27.3 to -17.1, 27.3 to -4.8, 14.7 to -4.8, 14.7 to -17.1, 27.3 to -17.1)
        ),
        Country(
            "Mauritius", -20.3484, 57.5522, Color(0xFFFF5722),
            listOf(-19.9 to 57.3, -19.9 to 57.8, -20.5 to 57.8, -20.5 to 57.3, -19.9 to 57.3)
        ),
        Country(
            "Morocco", 31.7917, -7.0926, Color(0xFFE57373),
            listOf(36.0 to -6.0, 36.0 to -1.0, 28.0 to -1.0, 28.0 to -13.0, 36.0 to -6.0)
        ),
        Country(
            "Mozambique", -18.6657, 35.5296, Color(0xFF00BCD4),
            listOf(-10.5 to 30.2, -10.5 to 40.8, -26.9 to 40.8, -26.9 to 30.2, -10.5 to 30.2)
        ),
        Country(
            "Namibia", -22.9576, 18.4904, Color(0xFF8BC34A),
            listOf(-17.0 to 12.0, -17.0 to 25.3, -29.0 to 25.3, -29.0 to 12.0, -17.0 to 12.0)
        ),
        Country(
            "Niger", 17.6078, 8.0817, Color(0xFFFF9800),
            listOf(23.5 to 0.2, 23.5 to 16.0, 11.7 to 16.0, 11.7 to 0.2, 23.5 to 0.2)
        ),
        Country(
            "Nigeria", 9.0820, 8.6753, Color(0xFF66BB6A),
            listOf(14.0 to 3.0, 14.0 to 14.0, 4.0 to 14.0, 4.0 to 3.0, 14.0 to 3.0)
        ),
        Country(
            "Rwanda", -1.9403, 29.8739, Color(0xFF2196F3),
            listOf(-1.1 to 29.0, -1.1 to 30.9, -2.8 to 30.9, -2.8 to 29.0, -1.1 to 29.0)
        ),
        Country(
            "Sao Tome and Principe", 0.1864, 6.6131, Color(0xFFE91E63),
            listOf(1.7 to 6.5, 1.7 to 7.5, -0.1 to 7.5, -0.1 to 6.5, 1.7 to 6.5)
        ),
        Country(
            "Senegal", 14.4974, -14.4524, Color(0xFF4CAF50),
            listOf(16.7 to -17.5, 16.7 to -11.4, 12.3 to -11.4, 12.3 to -17.5, 16.7 to -17.5)
        ),
        Country(
            "Seychelles", -4.6796, 55.4920, Color(0xFF9C27B0),
            listOf(-4.3 to 55.2, -4.3 to 55.8, -10.0 to 55.8, -10.0 to 55.2, -4.3 to 55.2)
        ),
        Country(
            "Sierra Leone", 8.4606, -11.7799, Color(0xFFFF5722),
            listOf(10.0 to -13.3, 10.0 to -10.3, 6.9 to -10.3, 6.9 to -13.3, 10.0 to -13.3)
        ),
        Country(
            "Somalia", 5.1521, 46.1996, Color(0xFF00BCD4),
            listOf(12.0 to 41.0, 12.0 to 51.4, -1.7 to 51.4, -1.7 to 41.0, 12.0 to 41.0)
        ),
        Country(
            "South Africa", -30.5595, 22.9375, Color(0xFFCDDC39),
            listOf(
                -22.0 to 17.0,
                -26.0 to 16.0,
                -29.0 to 17.0,
                -34.0 to 18.0,
                -34.0 to 27.0,
                -31.0 to 32.0,
                -26.0 to 32.0,
                -22.0 to 30.0,
                -22.0 to 17.0
            )
        ),
        Country(
            "South Sudan", 6.8770, 31.3070, Color(0xFF8BC34A),
            listOf(12.2 to 24.0, 12.2 to 36.0, 3.5 to 36.0, 3.5 to 24.0, 12.2 to 24.0)
        ),
        Country(
            "Sudan", 12.8628, 30.2176, Color(0xFFBA68C8),
            listOf(22.0 to 22.0, 22.0 to 38.0, 9.0 to 38.0, 9.0 to 22.0, 22.0 to 22.0)
        ),
        Country(
            "Tanzania", -6.3690, 34.8888, Color(0xFF9CCC65),
            listOf(-1.0 to 30.0, -1.0 to 40.0, -12.0 to 40.0, -12.0 to 30.0, -1.0 to 30.0)
        ),
        Country(
            "Togo", 8.6195, 0.8248, Color(0xFFFF9800),
            listOf(11.1 to -0.1, 11.1 to 1.8, 6.1 to 1.8, 6.1 to -0.1, 11.1 to -0.1)
        ),
        Country(
            "Tunisia", 33.8869, 9.5375, Color(0xFF2196F3),
            listOf(37.5 to 7.5, 37.5 to 11.6, 30.2 to 11.6, 30.2 to 7.5, 37.5 to 7.5)
        ),
        Country(
            "Uganda", 1.3733, 32.2903, Color(0xFFE91E63),
            listOf(4.2 to 29.6, 4.2 to 35.0, -1.5 to 35.0, -1.5 to 29.6, 4.2 to 29.6)
        ),
        Country(
            "Zambia", -13.1339, 27.8493, Color(0xFF4CAF50),
            listOf(-8.2 to 22.0, -8.2 to 34.0, -18.1 to 34.0, -18.1 to 22.0, -8.2 to 22.0)
        ),
        Country(
            "Zimbabwe", -19.0154, 29.1549, Color(0xFF9C27B0),
            listOf(-15.6 to 25.2, -15.6 to 33.1, -22.4 to 33.1, -22.4 to 25.2, -15.6 to 25.2)
        ),

        // Middle East (15)
        Country(
            "Afghanistan", 33.9391, 67.7100, Color(0xFF9C27B0),
            listOf(38.0 to 61.0, 38.0 to 75.0, 29.0 to 75.0, 29.0 to 61.0, 38.0 to 61.0)
        ),
        Country(
            "Armenia", 40.0691, 45.0382, Color(0xFFFF5722),
            listOf(41.3 to 43.4, 41.3 to 46.6, 38.8 to 46.6, 38.8 to 43.4, 41.3 to 43.4)
        ),
        Country(
            "Azerbaijan", 40.1431, 47.5769, Color(0xFF00BCD4),
            listOf(41.9 to 44.8, 41.9 to 50.4, 38.4 to 50.4, 38.4 to 44.8, 41.9 to 44.8)
        ),
        Country(
            "Bahrain", 26.0667, 50.5577, Color(0xFF8BC34A),
            listOf(26.3 to 50.4, 26.3 to 50.7, 25.8 to 50.7, 25.8 to 50.4, 26.3 to 50.4)
        ),
        Country(
            "Georgia", 42.3154, 43.3569, Color(0xFFFF9800),
            listOf(43.6 to 40.0, 43.6 to 46.7, 41.0 to 46.7, 41.0 to 40.0, 43.6 to 40.0)
        ),
        Country(
            "Iran", 32.4279, 53.6880, Color(0xFFAED581),
            listOf(40.0 to 44.0, 40.0 to 63.0, 25.0 to 63.0, 25.0 to 44.0, 40.0 to 44.0)
        ),
        Country(
            "Iraq", 33.2232, 43.6793, Color(0xFFFFD54F),
            listOf(37.0 to 38.0, 37.0 to 48.0, 29.0 to 48.0, 29.0 to 38.0, 37.0 to 38.0)
        ),
        Country(
            "Israel", 31.0461, 34.8516, Color(0xFF2196F3),
            listOf(33.3 to 34.3, 33.3 to 35.9, 29.5 to 35.9, 29.5 to 34.3, 33.3 to 34.3)
        ),
        Country(
            "Jordan", 30.5852, 36.2384, Color(0xFFE91E63),
            listOf(33.4 to 34.9, 33.4 to 39.3, 29.2 to 39.3, 29.2 to 34.9, 33.4 to 34.9)
        ),
        Country(
            "Kuwait", 29.3117, 47.4818, Color(0xFF4CAF50),
            listOf(30.1 to 46.5, 30.1 to 48.5, 28.5 to 48.5, 28.5 to 46.5, 30.1 to 46.5)
        ),
        Country(
            "Lebanon", 33.8547, 35.8623, Color(0xFF9C27B0),
            listOf(34.7 to 35.1, 34.7 to 36.6, 33.1 to 36.6, 33.1 to 35.1, 34.7 to 35.1)
        ),
        Country(
            "Oman", 21.4735, 55.9754, Color(0xFFFF5722),
            listOf(26.4 to 52.0, 26.4 to 60.0, 16.6 to 60.0, 16.6 to 52.0, 26.4 to 52.0)
        ),
        Country(
            "Palestine", 31.9522, 35.2332, Color(0xFF00BCD4),
            listOf(32.5 to 34.9, 32.5 to 35.6, 31.2 to 35.6, 31.2 to 34.9, 32.5 to 34.9)
        ),
        Country(
            "Qatar", 25.3548, 51.1839, Color(0xFF8BC34A),
            listOf(26.2 to 50.7, 26.2 to 51.6, 24.5 to 51.6, 24.5 to 50.7, 26.2 to 50.7)
        ),
        Country(
            "Saudi Arabia", 23.8859, 45.0792, Color(0xFF4DB6AC),
            listOf(32.0 to 35.0, 32.0 to 55.0, 16.0 to 55.0, 16.0 to 35.0, 32.0 to 35.0)
        ),
        Country(
            "Syria", 34.8021, 38.9968, Color(0xFFFF8A65),
            listOf(37.0 to 36.0, 37.0 to 42.0, 33.0 to 42.0, 33.0 to 36.0, 37.0 to 36.0)
        ),
        Country(
            "Turkey", 38.9637, 35.2433, Color(0xFFEF5350),
            listOf(42.0 to 27.0, 42.0 to 45.0, 36.0 to 45.0, 36.0 to 26.0, 42.0 to 27.0)
        ),
        Country(
            "United Arab Emirates", 23.4241, 53.8478, Color(0xFFFF9800),
            listOf(26.1 to 51.5, 26.1 to 56.4, 22.6 to 56.4, 22.6 to 51.5, 26.1 to 51.5)
        ),
        Country(
            "Yemen", 15.5527, 48.5164, Color(0xFFBA68C8),
            listOf(19.0 to 42.0, 19.0 to 54.0, 12.0 to 54.0, 12.0 to 42.0, 19.0 to 42.0)
        ),

        // Asia (47)
        Country(
            "Bangladesh", 23.6850, 90.3563, Color(0xFFFDD835),
            listOf(26.0 to 88.0, 26.0 to 93.0, 21.0 to 93.0, 21.0 to 88.0, 26.0 to 88.0)
        ),
        Country(
            "Bhutan", 27.5142, 90.4336, Color(0xFF9C27B0),
            listOf(28.4 to 89.0, 28.4 to 92.1, 26.7 to 92.1, 26.7 to 89.0, 28.4 to 89.0)
        ),
        Country(
            "Brunei", 4.5353, 114.7277, Color(0xFFFF5722),
            listOf(5.1 to 114.1, 5.1 to 115.4, 4.0 to 115.4, 4.0 to 114.1, 5.1 to 114.1)
        ),
        Country(
            "Cambodia", 12.5657, 104.9910, Color(0xFF00BCD4),
            listOf(14.7 to 102.3, 14.7 to 107.6, 10.4 to 107.6, 10.4 to 102.3, 14.7 to 102.3)
        ),
        Country(
            "China", 35.8617, 104.1954, Color(0xFFF44336),
            listOf(
                53.0 to 125.0,
                50.0 to 135.0,
                42.0 to 131.0,
                40.0 to 124.0,
                32.0 to 121.0,
                23.0 to 110.0,
                20.0 to 100.0,
                28.0 to 87.0,
                35.0 to 80.0,
                42.0 to 80.0,
                45.0 to 87.0,
                48.0 to 97.0,
                50.0 to 115.0,
                53.0 to 125.0
            )
        ),
        Country(
            "India", 20.5937, 78.9629, Color(0xFFFF9800),
            listOf(
                37.0 to 74.0,
                36.0 to 75.5,
                35.5 to 77.0,
                35.0 to 78.5,
                34.0 to 79.0,
                33.5 to 80.0,
                32.0 to 81.0,
                30.5 to 81.5,
                30.0 to 80.5,
                29.5 to 81.0,
                28.5 to 84.0,
                28.0 to 86.0,
                28.5 to 88.5,
                29.0 to 90.5,
                28.5 to 92.5,
                28.0 to 94.0,
                27.5 to 95.5,
                27.0 to 96.5,
                26.5 to 97.0,
                25.5 to 95.5,
                24.5 to 93.5,
                24.0 to 92.5,
                23.5 to 91.5,
                23.0 to 90.5,
                22.5 to 89.5,
                22.0 to 88.5,
                21.0 to 88.0,
                19.0 to 85.0,
                17.5 to 83.0,
                15.0 to 81.0,
                13.0 to 80.5,
                11.5 to 79.5,
                10.0 to 79.0,
                8.5 to 77.5,
                8.1 to 77.0,
                8.5 to 76.5,
                10.0 to 76.0,
                11.5 to 75.5,
                13.0 to 75.0,
                15.0 to 74.0,
                17.0 to 73.5,
                19.0 to 73.0,
                21.0 to 72.5,
                22.0 to 72.0,
                23.0 to 69.5,
                24.0 to 68.5,
                24.5 to 68.0,
                25.5 to 69.0,
                27.0 to 70.5,
                28.5 to 71.0,
                30.0 to 71.5,
                31.5 to 73.0,
                33.0 to 74.0,
                35.0 to 74.5,
                37.0 to 74.0
            )
        ),
        Country(
            "Indonesia", -0.7893, 113.9213, Color(0xFF4CAF50),
            listOf(6.0 to 95.0, 6.0 to 141.0, -11.0 to 141.0, -11.0 to 95.0, 6.0 to 95.0)
        ),
        Country(
            "Japan", 36.2048, 138.2529, Color(0xFFE91E63),
            listOf(
                45.0 to 142.0,
                42.0 to 145.0,
                38.0 to 141.0,
                35.0 to 139.0,
                33.0 to 130.0,
                35.0 to 133.0,
                38.0 to 138.0,
                42.0 to 140.0,
                45.0 to 142.0
            )
        ),
        Country(
            "Kazakhstan", 48.0196, 66.9237, Color(0xFF00BCD4),
            listOf(55.0 to 47.0, 55.0 to 87.0, 40.0 to 87.0, 40.0 to 47.0, 55.0 to 47.0)
        ),
        Country(
            "Kyrgyzstan", 41.2044, 74.7661, Color(0xFF8BC34A),
            listOf(43.3 to 69.3, 43.3 to 80.3, 39.2 to 80.3, 39.2 to 69.3, 43.3 to 69.3)
        ),
        Country(
            "Laos", 19.8563, 102.4955, Color(0xFFFF9800),
            listOf(22.5 to 100.1, 22.5 to 107.7, 14.0 to 107.7, 14.0 to 100.1, 22.5 to 100.1)
        ),
        Country(
            "Malaysia", 4.2105, 101.9758, Color(0xFF3F51B5),
            listOf(
                6.5 to 100.2,
                6.7 to 101.0,
                6.5 to 102.5,
                5.8 to 102.7,
                5.0 to 103.0,
                4.0 to 103.5,
                3.0 to 103.7,
                1.5 to 104.0,
                1.3 to 103.6,
                1.5 to 103.3,
                2.0 to 102.5,
                3.0 to 101.7,
                4.0 to 101.0,
                5.0 to 100.5,
                6.0 to 100.3,
                6.5 to 100.2,
                7.0 to 115.0,
                6.5 to 117.5,
                5.5 to 118.5,
                4.5 to 118.0,
                4.0 to 117.0,
                3.5 to 115.5,
                2.5 to 113.5,
                1.5 to 111.0,
                2.0 to 110.5,
                3.0 to 110.3,
                4.5 to 114.0,
                5.5 to 115.5,
                6.0 to 116.0,
                7.0 to 115.0
            )
        ),
        Country(
            "Maldives", 3.2028, 73.2207, Color(0xFF2196F3),
            listOf(7.1 to 72.6, 7.1 to 73.8, -0.7 to 73.8, -0.7 to 72.6, 7.1 to 72.6)
        ),
        Country(
            "Mongolia", 46.8625, 103.8467, Color(0xFF795548),
            listOf(52.0 to 88.0, 52.0 to 120.0, 42.0 to 120.0, 42.0 to 88.0, 52.0 to 88.0)
        ),
        Country(
            "Myanmar", 21.9162, 95.9560, Color(0xFFFFEB3B),
            listOf(28.0 to 92.0, 28.0 to 101.0, 10.0 to 101.0, 10.0 to 92.0, 28.0 to 92.0)
        ),
        Country(
            "Nepal", 28.3949, 84.1240, Color(0xFFD81B60),
            listOf(31.0 to 80.0, 31.0 to 88.0, 26.0 to 88.0, 26.0 to 80.0, 31.0 to 80.0)
        ),
        Country(
            "North Korea", 40.3399, 127.5101, Color(0xFF5D4037),
            listOf(43.0 to 124.0, 43.0 to 131.0, 38.0 to 131.0, 38.0 to 124.0, 43.0 to 124.0)
        ),
        Country(
            "Pakistan", 30.3753, 69.3451, Color(0xFF8BC34A),
            listOf(37.0 to 61.0, 37.0 to 77.0, 24.0 to 77.0, 24.0 to 61.0, 37.0 to 61.0)
        ),
        Country(
            "Philippines", 12.8797, 121.7740, Color(0xFFE91E63),
            listOf(21.0 to 120.0, 21.0 to 127.0, 5.0 to 127.0, 5.0 to 120.0, 21.0 to 120.0)
        ),
        Country(
            "Singapore", 1.3521, 103.8198, Color(0xFF9C27B0),
            listOf(1.5 to 103.6, 1.5 to 104.0, 1.2 to 104.0, 1.2 to 103.6, 1.5 to 103.6)
        ),
        Country(
            "South Korea", 35.9078, 127.7669, Color(0xFF00897B),
            listOf(38.0 to 125.0, 38.0 to 130.0, 33.0 to 130.0, 33.0 to 125.0, 38.0 to 125.0)
        ),
        Country(
            "Sri Lanka", 7.8731, 80.7718, Color(0xFFFF5722),
            listOf(10.0 to 79.7, 10.0 to 82.0, 6.0 to 82.0, 6.0 to 79.7, 10.0 to 79.7)
        ),
        Country(
            "Tajikistan", 38.8610, 71.2761, Color(0xFF00BCD4),
            listOf(41.0 to 67.4, 41.0 to 75.1, 36.7 to 75.1, 36.7 to 67.4, 41.0 to 67.4)
        ),
        Country(
            "Thailand", 15.8700, 100.9925, Color(0xFFFF5722),
            listOf(21.0 to 98.0, 21.0 to 106.0, 6.0 to 106.0, 6.0 to 98.0, 21.0 to 98.0)
        ),
        Country(
            "Timor-Leste", -8.8742, 125.7275, Color(0xFF8BC34A),
            listOf(-8.3 to 124.0, -8.3 to 127.3, -9.5 to 127.3, -9.5 to 124.0, -8.3 to 124.0)
        ),
        Country(
            "Turkmenistan", 38.9697, 59.5563, Color(0xFFFF9800),
            listOf(42.8 to 52.5, 42.8 to 66.7, 35.1 to 66.7, 35.1 to 52.5, 42.8 to 52.5)
        ),
        Country(
            "Uzbekistan", 41.3775, 64.5853, Color(0xFF2196F3),
            listOf(45.6 to 56.0, 45.6 to 73.1, 37.2 to 73.1, 37.2 to 56.0, 45.6 to 56.0)
        ),
        Country(
            "Vietnam", 14.0583, 108.2772, Color(0xFFFFC107),
            listOf(24.0 to 102.0, 24.0 to 110.0, 9.0 to 110.0, 9.0 to 102.0, 24.0 to 102.0)
        ),

        // Oceania (14)
        Country(
            "Australia", -25.2744, 133.7751, Color(0xFF9C27B0),
            listOf(
                -10.0 to 142.0,
                -12.0 to 137.0,
                -18.0 to 122.0,
                -26.0 to 113.0,
                -35.0 to 115.0,
                -38.0 to 140.0,
                -38.0 to 150.0,
                -28.0 to 154.0,
                -20.0 to 149.0,
                -11.0 to 143.0,
                -10.0 to 142.0
            )
        ),
        Country(
            "Fiji", -17.7134, 178.0650, Color(0xFFFF5722),
            listOf(-16.0 to 177.0, -16.0 to 180.0, -19.0 to 180.0, -19.0 to 177.0, -16.0 to 177.0)
        ),
        Country(
            "Kiribati", -3.3704, -168.7340, Color(0xFF00BCD4),
            listOf(4.0 to -157.0, 4.0 to 173.0, -12.0 to 173.0, -12.0 to -157.0, 4.0 to -157.0)
        ),
        Country(
            "Marshall Islands", 7.1315, 171.1845, Color(0xFF8BC34A),
            listOf(14.6 to 165.0, 14.6 to 172.0, 5.6 to 172.0, 5.6 to 165.0, 14.6 to 165.0)
        ),
        Country(
            "Micronesia", 7.4256, 150.5508, Color(0xFFFF9800),
            listOf(10.0 to 138.0, 10.0 to 163.0, 1.0 to 163.0, 1.0 to 138.0, 10.0 to 138.0)
        ),
        Country(
            "Nauru", -0.5228, 166.9315, Color(0xFF2196F3),
            listOf(-0.5 to 166.9, -0.5 to 167.0, -0.6 to 167.0, -0.6 to 166.9, -0.5 to 166.9)
        ),
        Country(
            "New Zealand", -40.9006, 174.8860, Color(0xFF7CB342),
            listOf(-34.0 to 173.0, -34.0 to 179.0, -47.0 to 179.0, -47.0 to 166.0, -34.0 to 173.0)
        ),
        Country(
            "Palau", 7.5150, 134.5825, Color(0xFFE91E63),
            listOf(8.1 to 134.1, 8.1 to 134.7, 6.9 to 134.7, 6.9 to 134.1, 8.1 to 134.1)
        ),
        Country(
            "Papua New Guinea", -6.3150, 143.9555, Color(0xFFFF7043),
            listOf(-1.0 to 141.0, -1.0 to 156.0, -12.0 to 156.0, -12.0 to 141.0, -1.0 to 141.0)
        ),
        Country(
            "Samoa", -13.7590, -172.1046, Color(0xFF4CAF50),
            listOf(-13.4 to -172.8, -13.4 to -171.4, -14.1 to -171.4, -14.1 to -172.8, -13.4 to -172.8)
        ),
        Country(
            "Solomon Islands", -9.6457, 160.1562, Color(0xFF9C27B0),
            listOf(-6.6 to 156.0, -6.6 to 163.0, -11.9 to 163.0, -11.9 to 156.0, -6.6 to 156.0)
        ),
        Country(
            "Tonga", -21.1789, -175.1982, Color(0xFFFF5722),
            listOf(-15.6 to -175.0, -15.6 to -173.9, -22.0 to -173.9, -22.0 to -175.0, -15.6 to -175.0)
        ),
        Country(
            "Tuvalu", -7.1095, 177.6493, Color(0xFF00BCD4),
            listOf(-5.7 to 176.1, -5.7 to 179.9, -10.8 to 179.9, -10.8 to 176.1, -5.7 to 176.1)
        ),
        Country(
            "Vanuatu", -15.3767, 166.9592, Color(0xFF8BC34A),
            listOf(-13.1 to 166.5, -13.1 to 170.2, -20.2 to 170.2, -20.2 to 166.5, -13.1 to 166.5)
        ),

        Country(
            "Antarctica", -82.8628, 135.0000, Color(0xFFE0F2F1), listOf(
                -63.0 to -60.0,
                -65.0 to -64.0,
                -68.0 to -67.0,
                -70.0 to -70.0,
                -72.0 to -75.0,
                -75.0 to -80.0,
                -77.0 to -90.0,
                -78.0 to -100.0,
                -78.5 to -120.0,
                -77.0 to -140.0,
                -75.0 to -150.0,
                -73.0 to -160.0,
                -70.0 to -170.0,
                -68.0 to -180.0,
                -68.0 to 180.0,
                -70.0 to 170.0,
                -73.0 to 160.0,
                -75.0 to 150.0,
                -77.0 to 140.0,
                -78.0 to 130.0,
                -78.5 to 120.0,
                -78.0 to 110.0,
                -77.0 to 100.0,
                -76.0 to 90.0,
                -75.0 to 80.0,
                -73.0 to 70.0,
                -71.0 to 60.0,
                -69.0 to 50.0,
                -68.0 to 40.0,
                -67.0 to 30.0,
                -66.0 to 20.0,
                -65.0 to 10.0,
                -64.0 to 0.0,
                -65.0 to -10.0,
                -67.0 to -20.0,
                -68.0 to -30.0,
                -69.0 to -40.0,
                -68.0 to -50.0,
                -66.0 to -55.0,
                -63.0 to -60.0
            )
        )
    )
}

