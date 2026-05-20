package com.example.rma1.views

import kotlin.math.pow

fun formatVotes(votes: Int): String{
    return if(votes >= 1_000_000_000){
        "${votes / 1000_000_000}B"
    }else if(votes >= 1_000_000){
        "${votes / 1_000_000}M"
    } else if (votes >= 1_000){
        "${votes / 1000}K"
    } else {
        votes.toString()
    }
}

fun formatBudget(budget: Int): String{
    if(budget >= 1_000_000_000){
        return "\$${budget / 1000_000_000}B"
    }else if(budget >= 1_000_000){
        return "\$${budget / 1_000_000}M"
    } else {
        return "\$${budget / 1000},${budget % 1000}"
    }
}

fun Float.truncate(decimals: Int): Float {
    val factor = 10f.pow(decimals)
    return (this * factor).toInt() / factor
}