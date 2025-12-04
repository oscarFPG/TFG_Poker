package com.ucm.statistics;

public record PlayerStats(
    double vpip,
    double prp,
    double ats,
    double threeBet,
    double wtsd,
    double wsd,
    double wwsf,
    double af,
    double handsWinRate,
    double tournamentsWinRate,
    FoldFrequency foldFrequency
){}