package com.example.dominos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource


val DominoRed = Color(0xFFE31837) // Domino's Red
val DominoBlue = Color(0xFF0055A5) // Domino's Blue
val DominoWhite = Color(0xFFFFFFFF) // White
val DominoBlack = Color(0xFF000000) // Black
val DominoDarkGray = Color(0xFF333333) // Dark Gray
val LightGrey = Color(0xFFEBEBEB) // Light grey color
val CouponGreen = Color(0xFF006400) // Green


@Composable
fun DominoMealApp() {
    var budget by remember { mutableStateOf("") }
    var couponCode by remember { mutableStateOf("") }
    val discountPercentage = if (couponCode == "DOM20") 0.2 else 0.0

    val combinations = remember(budget, discountPercentage) {
        calculateCombinations(budget.toIntOrNull() ?: 0)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGrey)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Domino's Meal Planner",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = DominoBlue
            )

            Spacer(modifier = Modifier.height(18.dp))

            Image(
                painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                contentDescription = "Domino's Logo",
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = budget,
                onValueChange = { budget = it },
                label = { Text("Enter your budget", color = DominoDarkGray) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = DominoWhite,
                    unfocusedContainerColor = DominoWhite,
                    focusedTextColor = DominoBlack,
                    unfocusedTextColor = DominoBlack,
                    focusedIndicatorColor = DominoBlue,
                    unfocusedIndicatorColor = DominoRed
                )
            )

            Spacer(modifier = Modifier.height(16.dp))


            TextField(
                value = couponCode,
                onValueChange = { couponCode = it },
                label = { Text("Enter Coupon Code") },
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = DominoWhite,
                    unfocusedContainerColor = DominoWhite,
                    focusedTextColor = DominoBlack,
                    unfocusedTextColor = DominoBlack,
                    focusedIndicatorColor = DominoBlue,
                    unfocusedIndicatorColor = DominoRed
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (discountPercentage > 0) "Coupon Applied! 20% Off" else "Enter 'DOM20' for 20% off",
                color = if (discountPercentage > 0) CouponGreen else DominoRed,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(combinations) { combination ->
                    CombinationCard(combination, discountPercentage)
                }
            }
        }
    }

}


@Composable
fun CombinationCard(combination: List<MenuItem>, discountPercentage: Double) {
    val totalPrice = combination.sumOf { it.price }
    val discountAmount = (totalPrice * discountPercentage).toInt()
    val discountedPrice = (totalPrice - discountAmount)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = DominoWhite
        ),
        border = BorderStroke(1.dp, DominoBlue)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                combination.forEach { item ->
                    Text(text = "${item.name} - ₹${item.price}", fontSize = 16.sp, color = DominoDarkGray)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Total: ₹$totalPrice", fontSize = 18.sp, color = DominoBlue)
                if (discountPercentage > 0) {
                    Text(text = "Discounted Total: ₹$discountedPrice", fontSize = 18.sp, color = CouponGreen)
                }
            }

            Image(
                painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                contentDescription = "Domino's Logo",
                modifier = Modifier.size(50.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}


fun calculateCombinations(budget: Int): List<List<MenuItem>> {
    val validCombinations = mutableListOf<List<MenuItem>>()

    fun backtrack(start: Int, target: Int, path: MutableList<MenuItem>) {
        // Stop if we have already found 20 combinations
        if (validCombinations.size >= 20) return

        // If the current combination is within the budget range, add it to the list
        if (target in (budget - 10)..(budget + 10)) {
            validCombinations.add(path.toList())
        }

        // If the current target exceeds the budget + 10, stop further exploration
        if (target > budget + 10) return

        // Explore further combinations, ensuring no item is reused
        for (i in start until dominoMenu.size) {
            path.add(dominoMenu[i])
            backtrack(i + 1, target + dominoMenu[i].price, path) // Use i + 1 to avoid reusing the same item
            path.removeAt(path.size - 1)
        }
    }

    backtrack(0, 0, mutableListOf())
    return validCombinations
}

// Mock Menu
data class MenuItem(val name: String, val price: Int)

val dominoMenu = listOf(
    // Pizzas
    MenuItem("Margherita", 200),
    MenuItem("Pepperoni", 250),
    MenuItem("Veggie Paradise", 220),
    MenuItem("Cheese Burst", 300),
    MenuItem("Farmhouse", 280),
    MenuItem("Chicken Dominator", 350),
    MenuItem("Paneer Makhani", 270),
    MenuItem("Spicy Chicken", 320),
    MenuItem("Tandoori Veg", 240),
    MenuItem("Tandoori Chicken", 330),
    MenuItem("Double Cheese Margherita", 310),
    MenuItem("Mexican Green Wave", 290),
    MenuItem("Chicken Golden Delight", 360),
    MenuItem("Non-Veg Supreme", 380),
    MenuItem("Veg Extravaganza", 260),

    // Sides
    MenuItem("Garlic Breadsticks", 100),
    MenuItem("Stuffed Garlic Bread", 150),
    MenuItem("Paneer Zingy Parcel", 120),
    MenuItem("Chicken Wings", 180),
    MenuItem("Potato Wedges", 90),
    MenuItem("Chicken Pepperoni Stuffed Garlic Bread", 200),
    MenuItem("Veg Pasta Italiano White", 130),
    MenuItem("Non-Veg Pasta Italiano White", 160),
    MenuItem("Veg Pasta Italiano Red", 130),
    MenuItem("Non-Veg Pasta Italiano Red", 160),

    // Desserts
    MenuItem("Choco Lava Cake", 110),
    MenuItem("Butterscotch Mousse Cake", 140),
    MenuItem("New York Cheesecake", 170),
    MenuItem("Dark Fantasy", 120),
    MenuItem("Chocolate Brownie", 100),
    MenuItem("Vanilla Ice Cream", 80),
    MenuItem("Strawberry Ice Cream", 80),
    MenuItem("Chocolate Ice Cream", 80),

    // Beverages
    MenuItem("Pepsi 500ml", 60),
    MenuItem("Mirinda 500ml", 60),
    MenuItem("7Up 500ml", 60),
    MenuItem("Mountain Dew 500ml", 60),
    MenuItem("Water Bottle 1L", 40),
    MenuItem("Iced Tea", 70),
    MenuItem("Cold Coffee", 90),
    MenuItem("Orange Juice", 80),
    MenuItem("Mango Juice", 80),

    // Combos
    MenuItem("Meal for 2: 2 Medium Pizzas + Garlic Bread + Pepsi", 800),
    MenuItem("Meal for 4: 4 Medium Pizzas + Stuffed Garlic Bread + Pepsi", 1500),
    MenuItem("Snack Combo: Garlic Bread + Potato Wedges + Pepsi", 300),
    MenuItem("Dessert Combo: Choco Lava Cake + Brownie + Ice Cream", 250),
    MenuItem("Family Combo: 1 Large Pizza + 1 Medium Pizza + Garlic Bread + Pepsi", 1200)
)