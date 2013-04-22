create database dataanalysis;

CREATE TABLE `company` (
  `CompanyID` INT NOT NULL,
  `asianOrder` INT DEFAULT NULL,
  `isHalf` INT DEFAULT NULL,
  `isLetgoal` INT DEFAULT NULL,
  `isStandard` INT DEFAULT NULL,
  `isTotalScore` INT DEFAULT NULL,
  `msrepl_tran_version` VARCHAR(255) DEFAULT NULL,
  `name_Cn` VARCHAR(255) DEFAULT NULL,
  `name_E` VARCHAR(255) DEFAULT NULL,
  `name_short` VARCHAR(255) DEFAULT NULL,
  `overDownOrder` INT DEFAULT NULL,
  `standardOrder` INT DEFAULT NULL,
  `totalOdds_L` DECIMAL(10,2) DEFAULT NULL,
  `totalOdds_T` DECIMAL(10,2) DEFAULT NULL,
  PRIMARY KEY (`CompanyID`)
);

CREATE TABLE `cupmatch` (
  `ID` INT NOT NULL,
  `area` INT DEFAULT NULL,
  `content` VARCHAR(255) DEFAULT NULL,
  `cupMatch_Type` INT DEFAULT NULL,
  `grouping` VARCHAR(255) DEFAULT NULL,
  `isUpdate` INT DEFAULT NULL,
  `lineCount` INT DEFAULT NULL,
  `matchseason` VARCHAR(255) DEFAULT NULL,
  `sclassID` INT DEFAULT NULL,
  `strContent` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
);

CREATE TABLE `cupmatch_grouping` (
  `GroupID` INT NOT NULL,
  `GroupNum` INT DEFAULT NULL,
  `addDateTime` DATETIME DEFAULT NULL,
  `groupName` VARCHAR(255) DEFAULT NULL,
  `groupNameEn` VARCHAR(255) DEFAULT NULL,
  `groupName_F` VARCHAR(255) DEFAULT NULL,
  `isCurrentGroup` INT DEFAULT NULL,
  `isGroup` INT DEFAULT NULL,
  `lineCount` INT DEFAULT NULL,
  `lyMatch` VARCHAR(255) DEFAULT NULL,
  `matchSeason` VARCHAR(255) DEFAULT NULL,
  `sclassID` INT DEFAULT NULL,
  `taxis` INT DEFAULT NULL,
  PRIMARY KEY (`GroupID`)
);

CREATE TABLE `cupmatch_type` (
  `ID` INT NOT NULL,
  `add_match` INT DEFAULT NULL,
  `area` INT DEFAULT NULL,
  `curr_type` INT DEFAULT NULL,
  `eight` INT DEFAULT NULL,
  `elimination` INT DEFAULT NULL,
  `five` INT DEFAULT NULL,
  `four` INT DEFAULT NULL,
  `group_one` INT DEFAULT NULL,
  `group_two` INT DEFAULT NULL,
  `halfz_match` INT DEFAULT NULL,
  `j_match` INT DEFAULT NULL,
  `jj_match` INT DEFAULT NULL,
  `mc_match` INT DEFAULT NULL,
  `one` INT DEFAULT NULL,
  `out_w` INT DEFAULT NULL,
  `sclassID` INT DEFAULT NULL,
  `six` INT DEFAULT NULL,
  `sixteen` INT DEFAULT NULL,
  `thiry_two` INT DEFAULT NULL,
  `three` INT DEFAULT NULL,
  `two` INT DEFAULT NULL,
  `yuxuan` INT DEFAULT NULL,
  `z_match` INT DEFAULT NULL,
  PRIMARY KEY (`ID`)
);

CREATE TABLE `detailresult` (
  `ID` INT NOT NULL,
  `happenTime` INT DEFAULT NULL,
  `kind` INT DEFAULT NULL,
  `modifyTime` DATETIME DEFAULT NULL,
  `playerID` INT DEFAULT NULL,
  `playername` VARCHAR(255) DEFAULT NULL,
  `playernameTxt` VARCHAR(255) DEFAULT NULL,
  `playername_e` VARCHAR(255) DEFAULT NULL,
  `playername_j` VARCHAR(255) DEFAULT NULL,
  `scheduleID` INT DEFAULT NULL,
  `teamID` INT DEFAULT NULL,
  PRIMARY KEY (`ID`)
);

CREATE TABLE `europecompany` (
  `companyID` INT NOT NULL,
  `name_Cn` VARCHAR(255) DEFAULT NULL,
  `name_E` VARCHAR(255) DEFAULT NULL,
  `isPrimary` INT DEFAULT NULL,
  `isExchange` INT DEFAULT NULL,
  PRIMARY KEY (`companyID`)
);

CREATE TABLE `globalcache` (
  `id` VARCHAR(255) NOT NULL,
  `value` mediumtext,
  PRIMARY KEY (`id`)
);

CREATE TABLE `halfscore` (
  `ID` INT NOT NULL,
  `fail_Score` INT DEFAULT NULL,
  `flat_Score` INT DEFAULT NULL,
  `homeorguest` INT DEFAULT NULL,
  `matchseason` VARCHAR(255) DEFAULT NULL,
  `sclassID` INT DEFAULT NULL,
  `subSclassID` INT DEFAULT NULL,
  `teamID` INT DEFAULT NULL,
  `total_Guestscore` INT DEFAULT NULL,
  `total_Homescore` INT DEFAULT NULL,
  `win_Score` INT DEFAULT NULL,
  PRIMARY KEY (`ID`)
);

CREATE TABLE `letgoal` (
  `OddsID` INT NOT NULL,
  `closePan` INT DEFAULT NULL,
  `companyID` INT DEFAULT NULL,
  `downOdds` DECIMAL(10,3) DEFAULT NULL,
  `downOdds_Real` DECIMAL(10,3) DEFAULT NULL,
  `firstDownodds` DECIMAL(10,3) DEFAULT NULL,
  `firstGoal` DECIMAL(10,2) DEFAULT NULL,
  `firstUpodds` DECIMAL(10,3) DEFAULT NULL,
  `goal` DECIMAL(10,2) DEFAULT NULL,
  `goal_Real` DECIMAL(10,3) DEFAULT NULL,
  `modifyTime` DATETIME DEFAULT NULL,
  `result` INT DEFAULT NULL,
  `running` INT DEFAULT NULL,
  `scheduleID` INT DEFAULT NULL,
  `upOdds` DECIMAL(10,3) DEFAULT NULL,
  `upOdds_Real` DECIMAL(10,3) DEFAULT NULL,
  `zouDi` INT DEFAULT NULL,
  PRIMARY KEY (`OddsID`)
);

CREATE TABLE `letgoaldetail` (
  `ID` INT NOT NULL,
  `downOdds` DECIMAL(10,3) DEFAULT NULL,
  `goal` DECIMAL(10,3) DEFAULT NULL,
  `isEarly` INT DEFAULT NULL,
  `modifyTime` DATETIME DEFAULT NULL,
  `oddsID` INT DEFAULT NULL,
  `upOdds` DECIMAL(10,3) DEFAULT NULL,
  PRIMARY KEY (`ID`)
);

CREATE TABLE `letgoalhalf` (
  `OddsID` INT NOT NULL,
  `companyID` INT DEFAULT NULL,
  `downOdds` DECIMAL(10,3) DEFAULT NULL,
  `firstDownodds` DECIMAL(10,3) DEFAULT NULL,
  `firstGoal` DECIMAL(10,2) DEFAULT NULL,
  `firstUpodds` DECIMAL(10,3) DEFAULT NULL,
  `goal` DECIMAL(10,2) DEFAULT NULL,
  `modifyTime` DATETIME DEFAULT NULL,
  `scheduleID` INT DEFAULT NULL,
  `upOdds` DECIMAL(10,3) DEFAULT NULL,
  `zouDi` INT DEFAULT NULL,
  PRIMARY KEY (`OddsID`)
);

CREATE TABLE `letgoalhalfdetail` (
  `ID` INT NOT NULL,
  `downOdds` DECIMAL(10,3) DEFAULT NULL,
  `goal` DECIMAL(10,2) DEFAULT NULL,
  `isEarly` INT DEFAULT NULL,
  `modifyTime` DATETIME DEFAULT NULL,
  `oddsID` INT DEFAULT NULL,
  `upOdds` DECIMAL(10,3) DEFAULT NULL,
  PRIMARY KEY (`ID`)
);

CREATE TABLE `lotcheckswitch` (
  `lotno` VARCHAR(255) NOT NULL,
  `state` INT NOT NULL,
  PRIMARY KEY (`lotno`)
);

CREATE TABLE `player` (
  `PlayerID` INT NOT NULL,
  `birthday` DATETIME DEFAULT NULL,
  `country` VARCHAR(255) DEFAULT NULL,
  `countryEn` VARCHAR(255) DEFAULT NULL,
  `health` VARCHAR(255) DEFAULT NULL,
  `introduce` VARCHAR(255) DEFAULT NULL,
  `isChecked` INT DEFAULT NULL,
  `kind` INT DEFAULT NULL,
  `modifyTime` DATETIME DEFAULT NULL,
  `name_7M` VARCHAR(255) DEFAULT NULL,
  `name_E` VARCHAR(255) DEFAULT NULL,
  `name_Es` VARCHAR(255) DEFAULT NULL,
  `name_F` VARCHAR(255) DEFAULT NULL,
  `name_J` VARCHAR(255) DEFAULT NULL,
  `name_T` VARCHAR(255) DEFAULT NULL,
  `name_Y` VARCHAR(255) DEFAULT NULL,
  `name_short` VARCHAR(255) DEFAULT NULL,
  `photo` VARCHAR(255) DEFAULT NULL,
  `tallness` INT DEFAULT NULL,
  `weight` INT DEFAULT NULL,
  PRIMARY KEY (`PlayerID`)
);

CREATE TABLE `player_zh` (
  `ZH_ID` INT NOT NULL,
  `hotSortNumber` INT DEFAULT NULL,
  `ifHot` INT DEFAULT NULL,
  `modifyTime` DATETIME DEFAULT NULL,
  `money` VARCHAR(255) DEFAULT NULL,
  `place` VARCHAR(255) DEFAULT NULL,
  `playerID` INT DEFAULT NULL,
  `score` INT DEFAULT NULL,
  `team` VARCHAR(255) DEFAULT NULL,
  `teamNow` VARCHAR(255) DEFAULT NULL,
  `transferTime` DATETIME DEFAULT NULL,
  `type` INT DEFAULT NULL,
  `xL_Date` VARCHAR(255) DEFAULT NULL,
  `zH_Season` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`ZH_ID`)
);

CREATE TABLE `playerinteam` (
  `ID` INT NOT NULL,
  `modifyTime` DATETIME DEFAULT NULL,
  `msrepl_tran_version` VARCHAR(255) DEFAULT NULL,
  `number` VARCHAR(255) DEFAULT NULL,
  `place` VARCHAR(255) DEFAULT NULL,
  `playerID` INT DEFAULT NULL,
  `playerName` VARCHAR(255) DEFAULT NULL,
  `score` INT DEFAULT NULL,
  `teamID` INT DEFAULT NULL,
  `teamName` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
);

CREATE TABLE `qiutanmatches` (
  `MATCHID` INT NOT NULL AUTO_INCREMENT,
  `away` VARCHAR(255) DEFAULT NULL,
  `awayID` INT DEFAULT NULL,
  `event` VARCHAR(255) DEFAULT NULL,
  `home` VARCHAR(255) DEFAULT NULL,
  `homeID` INT DEFAULT NULL,
  `iD_bet007` INT DEFAULT NULL,
  `id` VARCHAR(255) DEFAULT NULL,
  `issueNum` VARCHAR(255) DEFAULT NULL,
  `lotteryName` VARCHAR(255) DEFAULT NULL,
  `time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`MATCHID`)
);

CREATE TABLE `schedule` (
  `ScheduleID` INT NOT NULL,
  `SclassID` INT DEFAULT NULL,
  `MatchSeason` VARCHAR(255) DEFAULT NULL,
  `round` INT DEFAULT NULL,
  `grouping` VARCHAR(255) DEFAULT NULL,
  `HomeTeamID` INT DEFAULT NULL,
  `GuestTeamID` INT DEFAULT NULL,
  `HomeTeam` VARCHAR(255) DEFAULT NULL,
  `GuestTeam` VARCHAR(255) DEFAULT NULL,
  `Neutrality` INT DEFAULT NULL,
  `MatchTime` DATETIME DEFAULT NULL,
  `MatchTime2` DATETIME DEFAULT NULL,
  `Location` VARCHAR(255) DEFAULT NULL,
  `Home_Order` VARCHAR(255) DEFAULT NULL,
  `Guest_Order` VARCHAR(255) DEFAULT NULL,
  `MatchState` INT DEFAULT NULL,
  `WeatherIcon` INT DEFAULT NULL,
  `Weather` VARCHAR(255) DEFAULT NULL,
  `Temperature` VARCHAR(255) DEFAULT NULL,
  `TV` VARCHAR(255) DEFAULT NULL,
  `Umpire` VARCHAR(255) DEFAULT NULL,
  `Visitor` INT DEFAULT NULL,
  `HomeScore` INT DEFAULT NULL,
  `GuestScore` INT DEFAULT NULL,
  `HomeHalfScore` INT DEFAULT NULL,
  `GuestHalfScore` INT DEFAULT NULL,
  `Explain` TEXT,
  `Home_Red` INT DEFAULT NULL,
  `Guest_Red` INT DEFAULT NULL,
  `Home_Yellow` INT DEFAULT NULL,
  `Guest_Yellow` INT DEFAULT NULL,
  `bf_changetime` DATETIME DEFAULT NULL,
  `sequence` INT DEFAULT NULL,
  `IsWFC` INT DEFAULT NULL,
  `IsGoalC` INT DEFAULT NULL,
  `EuropeOddsShow` INT DEFAULT NULL,
  `shangpan` INT DEFAULT NULL,
  `OddsSequence` INT DEFAULT NULL,
  `AoShow` INT DEFAULT NULL,
  `bbinShow` INT DEFAULT NULL,
  `isanaly` INT DEFAULT NULL,
  `grouping2` VARCHAR(255) DEFAULT NULL,
  `Explain_en` TEXT,
  `bfShow` INT DEFAULT NULL,
  `subSclassID` INT DEFAULT NULL,
  `NowGoal_IsAnaly` INT DEFAULT NULL,
  `NowScore_IsAnaly` INT DEFAULT NULL,
  `Explainlist` VARCHAR(100) DEFAULT NULL,
  `event` VARCHAR(255) DEFAULT NULL,
  `avgH` DECIMAL(10,2) DEFAULT NULL,
  `avgS` DECIMAL(10,2) DEFAULT NULL,
  `avgG` DECIMAL(10,2) DEFAULT NULL,
  PRIMARY KEY (`ScheduleID`)
);

CREATE TABLE `sclass` (
  `sclassID` INT NOT NULL,
  `beginSeason` VARCHAR(255) DEFAULT NULL,
  `bf_IfDisp` INT DEFAULT NULL,
  `bf_simply_disp` INT DEFAULT NULL,
  `color` VARCHAR(255) DEFAULT NULL,
  `count_group` INT DEFAULT NULL,
  `count_round` INT DEFAULT NULL,
  `curr_matchSeason` VARCHAR(255) DEFAULT NULL,
  `curr_round` INT DEFAULT NULL,
  `getEspn` INT DEFAULT NULL,
  `ifHavePaper` INT DEFAULT NULL,
  `ifHaveSub` INT DEFAULT NULL,
  `ifShow` INT DEFAULT NULL,
  `ifSort` INT DEFAULT NULL,
  `ifindex` INT DEFAULT NULL,
  `ifstop` INT DEFAULT NULL,
  `infoID` INT DEFAULT NULL,
  `infoUrl` VARCHAR(255) DEFAULT NULL,
  `kind` INT DEFAULT NULL,
  `mode` INT DEFAULT NULL,
  `modifyTime` DATETIME DEFAULT NULL,
  `name_E` VARCHAR(255) DEFAULT NULL,
  `name_ES` VARCHAR(255) DEFAULT NULL,
  `name_F` VARCHAR(255) DEFAULT NULL,
  `name_FS` VARCHAR(255) DEFAULT NULL,
  `name_J` VARCHAR(255) DEFAULT NULL,
  `name_JS` VARCHAR(255) DEFAULT NULL,
  `name_S` VARCHAR(255) DEFAULT NULL,
  `nowGoalShow` INT DEFAULT NULL,
  `odds_ifDisp` INT DEFAULT NULL,
  `sclass_order` INT DEFAULT NULL,
  `sclass_pic` VARCHAR(255) DEFAULT NULL,
  `sclass_rule` VARCHAR(255) DEFAULT NULL,
  `sclass_ruleEn` VARCHAR(255) DEFAULT NULL,
  `sclass_sequence` INT DEFAULT NULL,
  `sclass_type` INT DEFAULT NULL,
  `subSclassID` INT DEFAULT NULL,
  `isRanking` INT DEFAULT NULL,
  PRIMARY KEY (`sclassID`)
);

CREATE TABLE `sclassinfo` (
  `InfoID` INT NOT NULL,
  `allOrder` INT DEFAULT NULL,
  `flagPic` VARCHAR(255) DEFAULT NULL,
  `infoOrder` INT DEFAULT NULL,
  `info_type` INT DEFAULT NULL,
  `modifyTime` DATETIME DEFAULT NULL,
  `nameCN` VARCHAR(255) DEFAULT NULL,
  `nameEN` VARCHAR(255) DEFAULT NULL,
  `nameFN` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`InfoID`)
);

CREATE TABLE `score` (
  `ID` INT NOT NULL,
  `cause` VARCHAR(255) DEFAULT NULL,
  `causeEn` VARCHAR(255) DEFAULT NULL,
  `deduct` INT DEFAULT NULL,
  `fail_Score` INT DEFAULT NULL,
  `flat_Score` INT DEFAULT NULL,
  `goal` INT DEFAULT NULL,
  `homeorguest` INT DEFAULT NULL,
  `matchseason` VARCHAR(255) DEFAULT NULL,
  `redCard` INT DEFAULT NULL,
  `sclassID` INT DEFAULT NULL,
  `subSclassID` INT DEFAULT NULL,
  `teamID` INT DEFAULT NULL,
  `total_Guestscore` INT DEFAULT NULL,
  `total_Homescore` INT DEFAULT NULL,
  `win_Score` INT DEFAULT NULL,
  PRIMARY KEY (`ID`)
);

CREATE TABLE `standard` (
  `OddsID` INT NOT NULL,
  `closePan` INT DEFAULT NULL,
  `companyID` INT DEFAULT NULL,
  `firstGuestWin` DECIMAL(10,2) DEFAULT NULL,
  `firstHomeWin` DECIMAL(10,2) DEFAULT NULL,
  `firstStandoff` DECIMAL(10,2) DEFAULT NULL,
  `guestWin` DECIMAL(10,2) DEFAULT NULL,
  `homeWin` DECIMAL(10,2) DEFAULT NULL,
  `modifyTime` DATETIME DEFAULT NULL,
  `result` INT DEFAULT NULL,
  `scheduleID` INT DEFAULT NULL,
  `standoff` DECIMAL(10,2) DEFAULT NULL,
  PRIMARY KEY (`OddsID`)
);

CREATE TABLE `standarddetail` (
  `ID` INT NOT NULL,
  `guestWin` DECIMAL(10,2) DEFAULT NULL,
  `homeWin` DECIMAL(10,2) DEFAULT NULL,
  `isEarly` INT DEFAULT NULL,
  `modifyTime` DATETIME DEFAULT NULL,
  `oddsID` INT DEFAULT NULL,
  `standoff` DECIMAL(10,2) DEFAULT NULL,
  PRIMARY KEY (`ID`)
);

CREATE TABLE `subsclass` (
  `subSclassID` INT NOT NULL,
  `count_round` INT DEFAULT NULL,
  `curr_round` INT DEFAULT NULL,
  `includeSeason` VARCHAR(255) DEFAULT NULL,
  `isAnalyScore` INT DEFAULT NULL,
  `isCurrentSclass` INT DEFAULT NULL,
  `isHaveScore` INT DEFAULT NULL,
  `sclassid` INT DEFAULT NULL,
  `sortNumber` INT DEFAULT NULL,
  `subName_Es` VARCHAR(255) DEFAULT NULL,
  `subName_Fs` VARCHAR(255) DEFAULT NULL,
  `subName_Js` VARCHAR(255) DEFAULT NULL,
  `subSclassName` VARCHAR(255) DEFAULT NULL,
  `subSclassNameEn` VARCHAR(255) DEFAULT NULL,
  `subSclass_F` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`subSclassID`)
);

CREATE TABLE `team` (
  `TeamID` INT NOT NULL,
  `address` VARCHAR(255) DEFAULT NULL,
  `area` VARCHAR(255) DEFAULT NULL,
  `areaEn` VARCHAR(255) DEFAULT NULL,
  `capacity` INT DEFAULT NULL,
  `drillmaster` VARCHAR(255) DEFAULT NULL,
  `flag` VARCHAR(255) DEFAULT NULL,
  `found_date` VARCHAR(255) DEFAULT NULL,
  `guestPoloShirt` VARCHAR(255) DEFAULT NULL,
  `gymnasium` VARCHAR(255) DEFAULT NULL,
  `gymnasiumEn` VARCHAR(255) DEFAULT NULL,
  `homePoloShirt` VARCHAR(255) DEFAULT NULL,
  `introduce` VARCHAR(255) DEFAULT NULL,
  `introduceEn` VARCHAR(255) DEFAULT NULL,
  `kind` INT DEFAULT NULL,
  `masterIntro` VARCHAR(255) DEFAULT NULL,
  `masterPic` VARCHAR(255) DEFAULT NULL,
  `modifyTime` DATETIME DEFAULT NULL,
  `name_Ch` VARCHAR(255) DEFAULT NULL,
  `name_E` VARCHAR(255) DEFAULT NULL,
  `name_EFull` VARCHAR(255) DEFAULT NULL,
  `name_F` VARCHAR(255) DEFAULT NULL,
  `name_J` VARCHAR(255) DEFAULT NULL,
  `name_Kr` VARCHAR(255) DEFAULT NULL,
  `name_Short` VARCHAR(255) DEFAULT NULL,
  `name_Singet` VARCHAR(255) DEFAULT NULL,
  `name_Spbo` VARCHAR(255) DEFAULT NULL,
  `name_T` VARCHAR(255) DEFAULT NULL,
  `name_Th` VARCHAR(255) DEFAULT NULL,
  `name_Vn` VARCHAR(255) DEFAULT NULL,
  `name_Y` VARCHAR(255) DEFAULT NULL,
  `sClassID` INT DEFAULT NULL,
  `url` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`TeamID`)
);

CREATE TABLE `totalscore` (
  `OddsID` INT NOT NULL,
  `closePan` INT DEFAULT NULL,
  `companyID` INT DEFAULT NULL,
  `downOdds` DECIMAL(10,2) DEFAULT NULL,
  `downOdds_real` DECIMAL(10,2) DEFAULT NULL,
  `firstDownodds` DECIMAL(10,2) DEFAULT NULL,
  `firstGoal` DECIMAL(10,2) DEFAULT NULL,
  `firstUpodds` DECIMAL(10,2) DEFAULT NULL,
  `goal` DECIMAL(10,2) DEFAULT NULL,
  `goal_real` DECIMAL(10,2) DEFAULT NULL,
  `modifyTime` DATETIME DEFAULT NULL,
  `result` INT DEFAULT NULL,
  `scheduleID` INT DEFAULT NULL,
  `upOdds` DECIMAL(10,2) DEFAULT NULL,
  `upOdds_real` DECIMAL(10,2) DEFAULT NULL,
  `zoudi` INT DEFAULT NULL,
  PRIMARY KEY (`OddsID`)
);

CREATE TABLE `totalscoredetail` (
  `ID` INT NOT NULL,
  `downOdds` DECIMAL(10,2) DEFAULT NULL,
  `goal` DECIMAL(10,2) DEFAULT NULL,
  `isEarly` INT DEFAULT NULL,
  `modifyTime` DATETIME DEFAULT NULL,
  `oddsID` INT DEFAULT NULL,
  `upOdds` DECIMAL(10,2) DEFAULT NULL,
  PRIMARY KEY (`ID`)
);

CREATE TABLE `totalscorehalf` (
  `OddsID` INT NOT NULL,
  `companyID` INT DEFAULT NULL,
  `downOdds` DECIMAL(10,2) DEFAULT NULL,
  `firstDownodds` DECIMAL(10,2) DEFAULT NULL,
  `firstGoal` DECIMAL(10,2) DEFAULT NULL,
  `firstUpodds` DECIMAL(10,2) DEFAULT NULL,
  `goal` DECIMAL(10,2) DEFAULT NULL,
  `modifyTime` DATETIME DEFAULT NULL,
  `scheduleID` INT DEFAULT NULL,
  `upOdds` DECIMAL(10,2) DEFAULT NULL,
  `zoudi` INT DEFAULT NULL,
  PRIMARY KEY (`OddsID`)
);

CREATE TABLE `totalscorehalfdetail` (
  `ID` INT NOT NULL,
  `downOdds` DECIMAL(10,2) DEFAULT NULL,
  `goal` DECIMAL(10,2) DEFAULT NULL,
  `isEarly` INT DEFAULT NULL,
  `modifyTime` DATETIME DEFAULT NULL,
  `oddsID` INT DEFAULT NULL,
  `upOdds` DECIMAL(10,2) DEFAULT NULL,
  PRIMARY KEY (`ID`)
);

CREATE TABLE `tseq` (
  `id` VARCHAR(100) NOT NULL,
  `seq` INT DEFAULT NULL,
  PRIMARY KEY (`id`)
);


ALTER TABLE detailresult ADD INDEX detailresult_scheduleid_index (scheduleID);
ALTER TABLE SCHEDULE ADD INDEX schedule_event_index (event);
ALTER TABLE SCHEDULE ADD INDEX schedule_sclassid_matchseason_index (SclassID, MatchSeason);
ALTER TABLE standard ADD INDEX standard_scheduleid_index (scheduleID);
ALTER TABLE letgoal ADD INDEX letgoal_scheduleid_index (scheduleID);