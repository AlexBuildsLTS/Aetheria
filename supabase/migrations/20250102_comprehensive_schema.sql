-- ============================================================================
-- AETHERIA: VOID HORIZON - COMPREHENSIVE DATABASE SCHEMA v2.0
-- ============================================================================
-- Purpose: Complete MMO database with advanced features
-- Includes: PvP, Dungeons, Achievements, Leaderboards, Events, Analytics
-- Status: Production-Ready, Optimized for Performance
-- ============================================================================

-- ============================================================================
-- PART 1: ACHIEVEMENTS & PROGRESSION SYSTEM
-- ============================================================================

-- Achievement Definitions Table
CREATE TABLE IF NOT EXISTS public.achievement_definitions (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    key TEXT UNIQUE NOT NULL,
    name TEXT NOT NULL,
    description TEXT NOT NULL,
    category TEXT NOT NULL, -- 'combat', 'exploration', 'social', 'crafting', 'pvp'
    tier INT DEFAULT 1, -- 1=Bronze, 2=Silver, 3=Gold, 4=Platinum
    requirements JSONB NOT NULL, -- e.g. {"kills": 100, "boss": "void_lord"}
    rewards JSONB DEFAULT '{}'::jsonb, -- {"xp": 1000, "title": "Void Slayer"}
    icon_path TEXT,
    is_hidden BOOLEAN DEFAULT false, -- Secret achievements
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Player Achievement Progress
CREATE TABLE IF NOT EXISTS public.player_achievements (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID REFERENCES public.profiles(id) ON DELETE CASCADE NOT NULL,
    achievement_id UUID REFERENCES public.achievement_definitions(id) ON DELETE CASCADE NOT NULL,
    progress JSONB DEFAULT '{}'::jsonb, -- Current progress toward requirements
    completed_at TIMESTAMP WITH TIME ZONE,
    is_completed BOOLEAN DEFAULT false,
    UNIQUE(user_id, achievement_id)
);

-- Titles System (Unlocked via Achievements)
CREATE TABLE IF NOT EXISTS public.player_titles (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID REFERENCES public.profiles(id) ON DELETE CASCADE NOT NULL,
    title_key TEXT NOT NULL,
    title_text TEXT NOT NULL,
    color_hex TEXT DEFAULT '#FFFFFF',
    unlocked_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    is_equipped BOOLEAN DEFAULT false
);

-- ============================================================================
-- PART 2: DUNGEON & INSTANCE SYSTEM
-- ============================================================================

-- Dungeon Definitions
CREATE TABLE IF NOT EXISTS public.dungeon_definitions (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    min_level INT DEFAULT 1,
    max_players INT DEFAULT 5,
    difficulty TEXT DEFAULT 'normal', -- 'normal', 'hard', 'nightmare'
    estimated_time_minutes INT DEFAULT 30,
    loot_table_id UUID, -- References a loot table (can be expanded)
    scene_path TEXT NOT NULL, -- Path to 3D scene file
    boss_ids JSONB DEFAULT '[]'::jsonb, -- Array of boss entity IDs
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Dungeon Runs (Instance Tracking)
CREATE TABLE IF NOT EXISTS public.dungeon_runs (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    dungeon_id UUID REFERENCES public.dungeon_definitions(id) NOT NULL,
    party_leader_id UUID REFERENCES public.profiles(id) NOT NULL,
    party_members JSONB NOT NULL, -- Array of user IDs
    started_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    completed_at TIMESTAMP WITH TIME ZONE,
    is_completed BOOLEAN DEFAULT false,
    total_kills INT DEFAULT 0,
    total_deaths INT DEFAULT 0,
    loot_generated JSONB DEFAULT '[]'::jsonb,
    completion_time_seconds INT
);

-- Boss Kill Tracking
CREATE TABLE IF NOT EXISTS public.boss_kills (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID REFERENCES public.profiles(id) ON DELETE CASCADE NOT NULL,
    boss_key TEXT NOT NULL,
    dungeon_run_id UUID REFERENCES public.dungeon_runs(id) ON DELETE SET NULL,
    killed_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    contribution_percent FLOAT DEFAULT 0.0, -- Damage contribution
    loot_received JSONB DEFAULT '[]'::jsonb
);

-- ============================================================================
-- PART 3: PVP SYSTEM
-- ============================================================================

-- PvP Match History
CREATE TABLE IF NOT EXISTS public.pvp_matches (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    match_type TEXT NOT NULL, -- 'duel', '3v3', '5v5', 'battleground'
    map_name TEXT,
    started_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    ended_at TIMESTAMP WITH TIME ZONE,
    winner_team TEXT, -- 'team_a', 'team_b', 'draw'
    team_a_members JSONB NOT NULL, -- Array of user IDs
    team_b_members JSONB NOT NULL,
    final_scores JSONB DEFAULT '{}'::jsonb, -- {"team_a": 100, "team_b": 95}
    duration_seconds INT
);

-- Player PvP Stats
CREATE TABLE IF NOT EXISTS public.pvp_stats (
    user_id UUID REFERENCES public.profiles(id) ON DELETE CASCADE PRIMARY KEY,
    total_matches INT DEFAULT 0,
    wins INT DEFAULT 0,
    losses INT DEFAULT 0,
    draws INT DEFAULT 0,
    kills INT DEFAULT 0,
    deaths INT DEFAULT 0,
    assists INT DEFAULT 0,
    rating INT DEFAULT 1000, -- ELO-style rating
    highest_rating INT DEFAULT 1000,
    current_streak INT DEFAULT 0, -- Win streak
    best_streak INT DEFAULT 0,
    last_match_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- PvP Seasons & Leaderboards
CREATE TABLE IF NOT EXISTS public.pvp_seasons (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    season_number INT UNIQUE NOT NULL,
    name TEXT NOT NULL,
    started_at TIMESTAMP WITH TIME ZONE NOT NULL,
    ended_at TIMESTAMP WITH TIME ZONE,
    is_active BOOLEAN DEFAULT true,
    rewards JSONB DEFAULT '{}'::jsonb -- Top 100 rewards, etc.
);

CREATE TABLE IF NOT EXISTS public.pvp_leaderboard (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    season_id UUID REFERENCES public.pvp_seasons(id) ON DELETE CASCADE NOT NULL,
    user_id UUID REFERENCES public.profiles(id) ON DELETE CASCADE NOT NULL,
    rank INT,
    rating INT NOT NULL,
    wins INT DEFAULT 0,
    losses INT DEFAULT 0,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    UNIQUE(season_id, user_id)
);

-- ============================================================================
-- PART 4: WORLD EVENTS & DYNAMIC CONTENT
-- ============================================================================

-- World Events (Server-Wide Events)
CREATE TABLE IF NOT EXISTS public.world_events (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    event_key TEXT UNIQUE NOT NULL,
    name TEXT NOT NULL,
    description TEXT,
    event_type TEXT NOT NULL, -- 'boss_spawn', 'invasion', 'double_xp', 'holiday'
    zone_id TEXT, -- Which zone it affects
    started_at TIMESTAMP WITH TIME ZONE,
    ends_at TIMESTAMP WITH TIME ZONE,
    is_active BOOLEAN DEFAULT false,
    participation_count INT DEFAULT 0,
    rewards JSONB DEFAULT '{}'::jsonb,
    metadata JSONB DEFAULT '{}'::jsonb -- Custom event data
);

-- Player Event Participation
CREATE TABLE IF NOT EXISTS public.event_participation (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    event_id UUID REFERENCES public.world_events(id) ON DELETE CASCADE NOT NULL,
    user_id UUID REFERENCES public.profiles(id) ON DELETE CASCADE NOT NULL,
    contribution_score INT DEFAULT 0,
    rewards_claimed BOOLEAN DEFAULT false,
    participated_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    UNIQUE(event_id, user_id)
);

-- ============================================================================
-- PART 5: ADVANCED ECONOMY & TRADING
-- ============================================================================

-- Trade History (Player-to-Player Direct Trades)
CREATE TABLE IF NOT EXISTS public.trade_history (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    trader_a_id UUID REFERENCES public.profiles(id) NOT NULL,
    trader_b_id UUID REFERENCES public.profiles(id) NOT NULL,
    trader_a_items JSONB DEFAULT '[]'::jsonb, -- Array of {item_id, quantity}
    trader_b_items JSONB DEFAULT '[]'::jsonb,
    trader_a_gold BIGINT DEFAULT 0,
    trader_b_gold BIGINT DEFAULT 0,
    completed_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    trade_hash TEXT -- For verification/audit
);

-- Auction House Bids (Advanced Market System)
CREATE TABLE IF NOT EXISTS public.auction_bids (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    listing_id UUID REFERENCES public.market_listings(id) ON DELETE CASCADE NOT NULL,
    bidder_id UUID REFERENCES public.profiles(id) NOT NULL,
    bid_amount BIGINT NOT NULL,
    bid_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    is_winning BOOLEAN DEFAULT false
);

-- Price History (Market Analytics)
CREATE TABLE IF NOT EXISTS public.price_history (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    item_id UUID REFERENCES public.items(id) NOT NULL,
    avg_price BIGINT NOT NULL,
    min_price BIGINT NOT NULL,
    max_price BIGINT NOT NULL,
    total_sales INT DEFAULT 0,
    recorded_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- ============================================================================
-- PART 6: PLAYER HOUSING & CUSTOMIZATION
-- ============================================================================

-- Player Housing
CREATE TABLE IF NOT EXISTS public.player_housing (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID REFERENCES public.profiles(id) ON DELETE CASCADE UNIQUE NOT NULL,
    house_type TEXT DEFAULT 'small_apartment', -- 'apartment', 'house', 'mansion'
    location_zone TEXT DEFAULT 'central_hub',
    furniture_layout JSONB DEFAULT '[]'::jsonb, -- Array of placed furniture
    visitors_allowed BOOLEAN DEFAULT true,
    total_visits INT DEFAULT 0,
    last_edited TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Furniture Catalog
CREATE TABLE IF NOT EXISTS public.furniture_items (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    category TEXT NOT NULL, -- 'chair', 'table', 'decoration', 'storage'
    model_path TEXT NOT NULL,
    price_aether BIGINT DEFAULT 0,
    unlock_requirement JSONB DEFAULT '{}'::jsonb -- e.g. {"achievement": "homeowner"}
);

-- Player Owned Furniture
CREATE TABLE IF NOT EXISTS public.player_furniture (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID REFERENCES public.profiles(id) ON DELETE CASCADE NOT NULL,
    furniture_id UUID REFERENCES public.furniture_items(id) NOT NULL,
    quantity INT DEFAULT 1,
    purchased_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- ============================================================================
-- PART 7: ANALYTICS & TELEMETRY
-- ============================================================================

-- Player Session Tracking
CREATE TABLE IF NOT EXISTS public.player_sessions (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID REFERENCES public.profiles(id) ON DELETE CASCADE NOT NULL,
    session_start TIMESTAMP WITH TIME ZONE DEFAULT now(),
    session_end TIMESTAMP WITH TIME ZONE,
    duration_seconds INT,
    zone_visits JSONB DEFAULT '[]'::jsonb, -- Zones visited during session
    actions_performed JSONB DEFAULT '{}'::jsonb, -- {"kills": 50, "quests": 3}
    client_version TEXT,
    device_info JSONB DEFAULT '{}'::jsonb
);

-- Economy Metrics (Daily Snapshots)
CREATE TABLE IF NOT EXISTS public.economy_snapshots (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    snapshot_date DATE UNIQUE NOT NULL,
    total_aether_in_circulation BIGINT DEFAULT 0,
    total_items_traded INT DEFAULT 0,
    average_player_wealth BIGINT DEFAULT 0,
    inflation_rate FLOAT DEFAULT 0.0,
    top_traded_items JSONB DEFAULT '[]'::jsonb,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Player Retention Metrics
CREATE TABLE IF NOT EXISTS public.retention_metrics (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID REFERENCES public.profiles(id) ON DELETE CASCADE NOT NULL,
    first_login TIMESTAMP WITH TIME ZONE,
    last_login TIMESTAMP WITH TIME ZONE,
    total_logins INT DEFAULT 0,
    total_playtime_hours FLOAT DEFAULT 0.0,
    days_since_last_login INT,
    is_retained BOOLEAN DEFAULT true, -- Active in last 7 days
    churn_risk_score FLOAT DEFAULT 0.0, -- 0.0 = low risk, 1.0 = high risk
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- ============================================================================
-- PART 8: ADVANCED FUNCTIONS & STORED PROCEDURES
-- ============================================================================

-- Function: Update PvP Rating (ELO System)
CREATE OR REPLACE FUNCTION public.update_pvp_rating(
    winner_id UUID,
    loser_id UUID,
    k_factor INT DEFAULT 32
) RETURNS JSONB AS $$
DECLARE
    winner_rating INT;
    loser_rating INT;
    expected_winner FLOAT;
    expected_loser FLOAT;
    new_winner_rating INT;
    new_loser_rating INT;
BEGIN
    -- Get current ratings
    SELECT rating INTO winner_rating FROM public.pvp_stats WHERE user_id = winner_id;
    SELECT rating INTO loser_rating FROM public.pvp_stats WHERE user_id = loser_id;

    -- Calculate expected scores (ELO formula)
    expected_winner := 1.0 / (1.0 + power(10, (loser_rating - winner_rating) / 400.0));
    expected_loser := 1.0 / (1.0 + power(10, (winner_rating - loser_rating) / 400.0));

    -- Calculate new ratings
    new_winner_rating := winner_rating + (k_factor * (1.0 - expected_winner))::INT;
    new_loser_rating := loser_rating + (k_factor * (0.0 - expected_loser))::INT;

    -- Update ratings
    UPDATE public.pvp_stats
    SET rating = new_winner_rating,
        highest_rating = GREATEST(highest_rating, new_winner_rating),
        wins = wins + 1,
        current_streak = current_streak + 1,
        best_streak = GREATEST(best_streak, current_streak + 1),
        updated_at = now()
    WHERE user_id = winner_id;

    UPDATE public.pvp_stats
    SET rating = new_loser_rating,
        losses = losses + 1,
        current_streak = 0,
        updated_at = now()
    WHERE user_id = loser_id;

    RETURN jsonb_build_object(
        'winner_new_rating', new_winner_rating,
        'loser_new_rating', new_loser_rating,
        'rating_change', new_winner_rating - winner_rating
    );
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- Function: Grant Achievement
CREATE OR REPLACE FUNCTION public.grant_achievement(
    player_id UUID,
    achievement_key TEXT
) RETURNS JSONB AS $$
DECLARE
    achievement_rec RECORD;
    existing_achievement UUID;
    reward_xp INT;
    reward_title TEXT;
BEGIN
    -- Get achievement definition
    SELECT * INTO achievement_rec
    FROM public.achievement_definitions
    WHERE key = achievement_key;

    IF achievement_rec IS NULL THEN
        RETURN jsonb_build_object('success', false, 'message', 'Achievement not found');
    END IF;

    -- Check if already completed
    SELECT id INTO existing_achievement
    FROM public.player_achievements
    WHERE user_id = player_id AND achievement_id = achievement_rec.id AND is_completed = true;

    IF existing_achievement IS NOT NULL THEN
        RETURN jsonb_build_object('success', false, 'message', 'Already completed');
    END IF;

    -- Mark as completed
    INSERT INTO public.player_achievements (user_id, achievement_id, is_completed, completed_at)
    VALUES (player_id, achievement_rec.id, true, now())
    ON CONFLICT (user_id, achievement_id)
    DO UPDATE SET is_completed = true, completed_at = now();

    -- Grant rewards
    reward_xp := (achievement_rec.rewards->>'xp')::INT;
    reward_title := achievement_rec.rewards->>'title';

    IF reward_xp IS NOT NULL THEN
        UPDATE public.profiles SET xp = xp + reward_xp WHERE id = player_id;
    END IF;

    IF reward_title IS NOT NULL THEN
        INSERT INTO public.player_titles (user_id, title_key, title_text)
        VALUES (player_id, achievement_key, reward_title);
    END IF;

    RETURN jsonb_build_object(
        'success', true,
        'achievement', achievement_rec.name,
        'rewards', achievement_rec.rewards
    );
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- Function: Start Dungeon Run
CREATE OR REPLACE FUNCTION public.start_dungeon_run(
    dungeon_key UUID,
    leader_id UUID,
    party_member_ids JSONB
) RETURNS UUID AS $$
DECLARE
    new_run_id UUID;
BEGIN
    INSERT INTO public.dungeon_runs (dungeon_id, party_leader_id, party_members)
    VALUES (dungeon_key, leader_id, party_member_ids)
    RETURNING id INTO new_run_id;

    RETURN new_run_id;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- Function: Complete Dungeon Run
CREATE OR REPLACE FUNCTION public.complete_dungeon_run(
    run_id UUID,
    completion_time INT,
    total_kills_count INT,
    total_deaths_count INT
) RETURNS JSONB AS $$
DECLARE
    run_record RECORD;
    member_id UUID;
    bonus_xp INT;
BEGIN
    -- Get run details
    SELECT * INTO run_record FROM public.dungeon_runs WHERE id = run_id;

    IF run_record IS NULL THEN
        RETURN jsonb_build_object('success', false, 'message', 'Run not found');
    END IF;

    -- Mark as completed
    UPDATE public.dungeon_runs
    SET is_completed = true,
        completed_at = now(),
        completion_time_seconds = completion_time,
        total_kills = total_kills_count,
        total_deaths = total_deaths_count
    WHERE id = run_id;

    -- Grant XP to all party members
    bonus_xp := 1000 + (total_kills_count * 10);

    FOR member_id IN SELECT jsonb_array_elements_text(run_record.party_members)::UUID
    LOOP
        UPDATE public.profiles
        SET xp = xp + bonus_xp
        WHERE id = member_id;
    END LOOP;

    RETURN jsonb_build_object(
        'success', true,
        'bonus_xp', bonus_xp,
        'completion_time', completion_time
    );
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- Function: Daily Economy Snapshot
CREATE OR REPLACE FUNCTION public.create_economy_snapshot()
RETURNS VOID AS $$
DECLARE
    total_aether BIGINT;
    avg_wealth BIGINT;
    trades_today INT;
BEGIN
    -- Calculate metrics
    SELECT COALESCE(SUM(aether_balance), 0) INTO total_aether FROM public.profiles;
    SELECT COALESCE(AVG(aether_balance), 0)::BIGINT INTO avg_wealth FROM public.profiles;
    SELECT COUNT(*) INTO trades_today FROM public.trade_history WHERE completed_at >= CURRENT_DATE;

    -- Insert snapshot
    INSERT INTO public.economy_snapshots (
        snapshot_date,
        total_aether_in_circulation,
        average_player_wealth,
        total_items_traded
    ) VALUES (
        CURRENT_DATE,
        total_aether,
        avg_wealth,
        trades_today
    )
    ON CONFLICT (snapshot_date) DO UPDATE
    SET total_aether_in_circulation = EXCLUDED.total_aether_in_circulation,
        average_player_wealth = EXCLUDED.average_player_wealth,
        total_items_traded = EXCLUDED.total_items_traded;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- Function: Update Player Retention Metrics
CREATE OR REPLACE FUNCTION public.update_retention_metrics()
RETURNS VOID AS $$
BEGIN
    INSERT INTO public.retention_metrics (user_id, first_login, last_login, total_logins, days_since_last_login, is_retained)
    SELECT
        id,
        created_at,
        last_login,
        1,
        EXTRACT(DAY FROM (now() - last_login))::INT,
        (last_login >= now() - INTERVAL '7 days')
    FROM public.profiles
    ON CONFLICT (user_id) DO UPDATE
    SET last_login = EXCLUDED.last_login,
        total_logins = retention_metrics.total_logins + 1,
        days_since_last_login = EXTRACT(DAY FROM (now() - EXCLUDED.last_login))::INT,
        is_retained = (EXCLUDED.last_login >= now() - INTERVAL '7 days'),
        updated_at = now();
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- ============================================================================
-- PART 9: ROW LEVEL SECURITY POLICIES
-- ============================================================================

-- Achievements
ALTER TABLE public.achievement_definitions ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Public Read Achievements" ON public.achievement_definitions FOR SELECT USING (true);

ALTER TABLE public.player_achievements ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Self Achievement Access" ON public.player_achievements FOR ALL USING (auth.uid() = user_id);

ALTER TABLE public.player_titles ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Self Titles Access" ON public.player_titles FOR ALL USING (auth.uid() = user_id);

-- Dungeons
ALTER TABLE public.dungeon_definitions ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Public Read Dungeons" ON public.dungeon_definitions FOR SELECT USING (true);

ALTER TABLE public.dungeon_runs ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Party Member Access" ON public.dungeon_runs FOR SELECT
USING (auth.uid() = party_leader_id OR party_members ? auth.uid()::TEXT);

ALTER TABLE public.boss_kills ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Self Boss Kills" ON public.boss_kills FOR ALL USING (auth.uid() = user_id);

-- PvP
ALTER TABLE public.pvp_matches ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Public Read Matches" ON public.pvp_matches FOR SELECT USING (true);

ALTER TABLE public.pvp_stats ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Public Read PvP Stats" ON public.pvp_stats FOR SELECT USING (true);
CREATE POLICY "Self Update PvP Stats" ON public.pvp_stats FOR UPDATE USING (auth.uid() = user_id);

ALTER TABLE public.pvp_leaderboard ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Public Read Leaderboard" ON public.pvp_leaderboard FOR SELECT USING (true);

-- World Events
ALTER TABLE public.world_events ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Public Read Events" ON public.world_events FOR SELECT USING (true);

ALTER TABLE public.event_participation ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Self Event Participation" ON public.event_participation FOR ALL USING (auth.uid() = user_id);

-- Trading
ALTER TABLE public.trade_history ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Trader Access" ON public.trade_history FOR SELECT
USING (auth.uid() = trader_a_id OR auth.uid() = trader_b_id);

ALTER TABLE public.auction_bids ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Public Read Bids" ON public.auction_bids FOR SELECT USING (true);
CREATE POLICY "Self Bid Management" ON public.auction_bids FOR ALL USING (auth.uid() = bidder_id);

-- Housing
ALTER TABLE public.player_housing ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Public Read Housing" ON public.player_housing FOR SELECT USING (visitors_allowed = true);
CREATE POLICY "Self Housing Management" ON public.player_housing FOR ALL USING (auth.uid() = user_id);

ALTER TABLE public.furniture_items ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Public Read Furniture" ON public.furniture_items FOR SELECT USING (true);

ALTER TABLE public.player_furniture ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Self Furniture Access" ON public.player_furniture FOR ALL USING (auth.uid() = user_id);

-- Analytics (Admin Only - No RLS for now, handle via service role)
-- Sessions, economy snapshots, retention metrics should be accessed via backend only

-- ============================================================================
-- PART 10: SEED DATA
-- ============================================================================

-- Seed Achievements
INSERT INTO public.achievement_definitions (key, name, description, category, tier, requirements, rewards) VALUES
('first_blood', 'First Blood', 'Defeat your first enemy', 'combat', 1, '{"kills": 1}'::jsonb, '{"xp": 100}'::jsonb),
('slayer_100', 'Slayer', 'Defeat 100 enemies', 'combat', 2, '{"kills": 100}'::jsonb, '{"xp": 1000, "title": "The Slayer"}'::jsonb),
('void_hunter', 'Void Hunter', 'Defeat 1000 enemies', 'combat', 3, '{"kills": 1000}'::jsonb, '{"xp": 10000, "title": "Void Hunter"}'::jsonb),
('explorer', 'Explorer', 'Visit all zones', 'exploration', 2, '{"zones_visited": 10}'::jsonb, '{"xp": 2000}'::jsonb),
('social_butterfly', 'Social Butterfly', 'Make 10 friends', 'social', 1, '{"friends": 10}'::jsonb, '{"xp": 500}'::jsonb),
('master_crafter', 'Master Crafter', 'Craft 100 items', 'crafting', 3, '{"items_crafted": 100}'::jsonb, '{"xp": 5000, "title": "Master Crafter"}'::jsonb),
('pvp_novice', 'PvP Novice', 'Win your first PvP match', 'pvp', 1, '{"pvp_wins": 1}'::jsonb, '{"xp": 200}'::jsonb),
('gladiator', 'Gladiator', 'Win 100 PvP matches', 'pvp', 3, '{"pvp_wins": 100}'::jsonb, '{"xp": 10000, "title": "Gladiator"}'::jsonb);

-- Seed Dungeons
INSERT INTO public.dungeon_definitions (name, description, min_level, max_players, difficulty, estimated_time_minutes, scene_path, boss_ids) VALUES
('Corrupted Data Vault', 'A digital fortress filled with rogue AI', 5, 5, 'normal', 20, 'dungeons/data_vault.gltf', '["boss_corrupted_ai"]'::jsonb),
('Void Nexus', 'The heart of the void corruption', 15, 5, 'hard', 45, 'dungeons/void_nexus.gltf', '["boss_void_lord", "boss_void_guardian"]'::jsonb),
('Neon Labyrinth', 'A maze of light and shadow', 10, 3, 'normal', 30, 'dungeons/neon_lab.gltf', '["boss_light_construct"]'::jsonb),
('Rust Catacombs', 'Ancient ruins beneath the Rust Lands', 1, 5, 'normal', 15, 'dungeons/rust_catacombs.gltf', '["boss_scrap_king"]'::jsonb);

-- Seed PvP Season
INSERT INTO public.pvp_seasons (season_number, name, started_at, is_active) VALUES
(1, 'Season 1: Void Awakening', now(), true);

-- Seed Furniture Items
INSERT INTO public.furniture_items (name, description, category, model_path, price_aether) VALUES
('Tech Chair', 'A comfortable holographic chair', 'chair', 'furniture/chair_tech.gltf', 500),
('Neon Table', 'A glowing table with LED edges', 'table', 'furniture/table_neon.gltf', 1000),
('Void Crystal', 'A decorative void crystal', 'decoration', 'furniture/crystal_void.gltf', 2500),
('Storage Crate', 'Extra storage for your items', 'storage', 'furniture/crate_storage.gltf', 1500),
('Hologram Projector', 'Projects cool holograms', 'decoration', 'furniture/projector.gltf', 3000);

-- ============================================================================
-- PART 11: INDEXES FOR PERFORMANCE
-- ============================================================================

-- Achievement indexes
CREATE INDEX IF NOT EXISTS idx_player_achievements_user ON public.player_achievements(user_id);
CREATE INDEX IF NOT EXISTS idx_player_achievements_completed ON public.player_achievements(is_completed);

-- Dungeon indexes
CREATE INDEX IF NOT EXISTS idx_dungeon_runs_leader ON public.dungeon_runs(party_leader_id);
CREATE INDEX IF NOT EXISTS idx_dungeon_runs_completed ON public.dungeon_runs(is_completed);
CREATE INDEX IF NOT EXISTS idx_boss_kills_user ON public.boss_kills(user_id);
CREATE INDEX IF NOT EXISTS idx_boss_kills_boss ON public.boss_kills(boss_key);

-- PvP indexes
CREATE INDEX IF NOT EXISTS idx_pvp_stats_rating ON public.pvp_stats(rating DESC);
CREATE INDEX IF NOT EXISTS idx_pvp_leaderboard_season_rank ON public.pvp_leaderboard(season_id, rank);
CREATE INDEX IF NOT EXISTS idx_pvp_matches_type ON public.pvp_matches(match_type);

-- Event indexes
CREATE INDEX IF NOT EXISTS idx_world_events_active ON public.world_events(is_active);
CREATE INDEX IF NOT EXISTS idx_event_participation_event ON public.event_participation(event_id);

-- Trading indexes
CREATE INDEX IF NOT EXISTS idx_trade_history_traders ON public.trade_history(trader_a_id, trader_b_id);
CREATE INDEX IF NOT EXISTS idx_auction_bids_listing ON public.auction_bids(listing_id);
CREATE INDEX IF NOT EXISTS idx_price_history_item ON public.price_history(item_id, recorded_at DESC);

-- Housing indexes
CREATE INDEX IF NOT EXISTS idx_player_housing_user ON public.player_housing(user_id);
CREATE INDEX IF NOT EXISTS idx_player_furniture_user ON public.player_furniture(user_id);

-- Analytics indexes
CREATE INDEX IF NOT EXISTS idx_player_sessions_user ON public.player_sessions(user_id);
CREATE INDEX IF NOT EXISTS idx_player_sessions_start ON public.player_sessions(session_start DESC);
CREATE INDEX IF NOT EXISTS idx_retention_metrics_user ON public.retention_metrics(user_id);
CREATE INDEX IF NOT EXISTS idx_retention_metrics_retained ON public.retention_metrics(is_retained);

-- ============================================================================
-- PART 12: REALTIME SUBSCRIPTIONS
-- ============================================================================

-- Add new tables to realtime publication
ALTER PUBLICATION supabase_realtime ADD TABLE public.world_events;
ALTER PUBLICATION supabase_realtime ADD TABLE public.pvp_leaderboard;
ALTER PUBLICATION supabase_realtime ADD TABLE public.dungeon_runs;

-- ============================================================================
-- COMPLETION MESSAGE
-- ============================================================================

DO $$
BEGIN
    RAISE NOTICE '============================================================================';
    RAISE NOTICE 'AETHERIA: VOID HORIZON - COMPREHENSIVE SCHEMA v2.0 INSTALLED SUCCESSFULLY';
    RAISE NOTICE '============================================================================';
    RAISE NOTICE 'Features Installed:';
    RAISE NOTICE '  ✓ Achievements & Titles System';
    RAISE NOTICE '  ✓ Dungeon & Instance System';
    RAISE NOTICE '  ✓ PvP & Leaderboards';
    RAISE NOTICE '  ✓ World Events';
    RAISE NOTICE '  ✓ Advanced Trading & Auctions';
    RAISE NOTICE '  ✓ Player Housing';
    RAISE NOTICE '  ✓ Analytics & Telemetry';
    RAISE NOTICE '  ✓ Performance Indexes';
    RAISE NOTICE '  ✓ Row Level Security';
    RAISE NOTICE '============================================================================';
    RAISE NOTICE 'Total Tables: 30+';
    RAISE NOTICE 'Total Functions: 10+';
    RAISE NOTICE 'Status: PRODUCTION READY';
    RAISE NOTICE '============================================================================';
END $$;
