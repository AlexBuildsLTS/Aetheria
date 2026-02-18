package com.aetheria.mmo.utils

/**
 * Game Constants
 * Centralized configuration values for game mechanics, physics, and networking
 */
object Constants {

    // ==================== Game Info ====================
    const val GAME_NAME = "Aetheria: Void Horizon"
    const val GAME_VERSION = "0.1.0-alpha"
    const val BUILD_NUMBER = 1

    // ==================== Display ====================
    const val WINDOW_WIDTH = 1920
    const val WINDOW_HEIGHT = 1080
    const val TARGET_FPS = 60
    const val VSYNC_ENABLED = true

    // ==================== Physics ====================
    const val GRAVITY = -9.81f
    const val TERMINAL_VELOCITY = -50f
    const val GROUND_FRICTION = 0.8f
    const val AIR_FRICTION = 0.95f
    const val MAX_PHYSICS_DELTA = 0.1f // Prevent physics explosions

    // ==================== Player ====================
    const val PLAYER_MOVE_SPEED = 5f
    const val PLAYER_SPRINT_MULTIPLIER = 1.5f
    const val PLAYER_JUMP_FORCE = 8f
    const val PLAYER_MAX_HEALTH = 100f
    const val PLAYER_MAX_SHIELD = 50f
    const val PLAYER_MAX_STAMINA = 100f
    const val PLAYER_HEALTH_REGEN = 5f // per second
    const val PLAYER_SHIELD_REGEN = 10f // per second
    const val PLAYER_STAMINA_REGEN = 20f // per second
    const val PLAYER_SHIELD_REGEN_DELAY = 3f // seconds after damage

    // ==================== Combat ====================
    const val BASE_ATTACK_DAMAGE = 10f
    const val CRITICAL_HIT_MULTIPLIER = 2f
    const val BASE_CRITICAL_CHANCE = 0.1f // 10%
    const val ATTACK_COOLDOWN = 1f // seconds
    const val ABILITY_GLOBAL_COOLDOWN = 0.5f // seconds
    const val MAX_COMBO_CHAIN = 5
    const val COMBO_TIMEOUT = 2f // seconds

    // ==================== Camera ====================
    const val CAMERA_FOV = 67f
    const val CAMERA_NEAR = 0.1f
    const val CAMERA_FAR = 1000f
    const val CAMERA_FOLLOW_SPEED = 5f
    const val CAMERA_ROTATION_SPEED = 2f
    const val CAMERA_ZOOM_MIN = 5f
    const val CAMERA_ZOOM_MAX = 30f
    const val CAMERA_ZOOM_DEFAULT = 15f

    // ==================== Networking ====================
    const val SERVER_TICK_RATE = 20 // ticks per second
    const val CLIENT_UPDATE_RATE = 60 // updates per second
    const val NETWORK_TIMEOUT = 10000L // milliseconds
    const val PING_INTERVAL = 1000L // milliseconds
    const val MAX_PACKET_SIZE = 1024 * 64 // 64KB
    const val SNAPSHOT_BUFFER_SIZE = 32 // Number of snapshots to keep for interpolation
    const val INTERPOLATION_DELAY = 100L // milliseconds (2 server ticks)
    const val PREDICTION_THRESHOLD = 50L // milliseconds

    // ==================== World ====================
    const val WORLD_SIZE = 1000f // units
    const val CHUNK_SIZE = 50f // units
    const val VIEW_DISTANCE = 200f // units
    const val ENTITY_DESPAWN_DISTANCE = 250f // units
    const val MAX_ENTITIES_PER_CHUNK = 100

    // ==================== Inventory ====================
    const val INVENTORY_SLOTS = 30
    const val HOTBAR_SLOTS = 10
    const val MAX_STACK_SIZE = 99
    const val STARTING_GOLD = 0

    // ==================== UI ====================
    const val UI_SCALE = 1f
    const val TOOLTIP_DELAY = 0.5f // seconds
    const val NOTIFICATION_DURATION = 3f // seconds
    const val CHAT_MESSAGE_LIMIT = 100
    const val CHAT_FADE_TIME = 10f // seconds

    // ==================== Audio ====================
    const val MASTER_VOLUME = 1f
    const val MUSIC_VOLUME = 0.7f
    const val SFX_VOLUME = 1f
    const val AMBIENT_VOLUME = 0.5f
    const val MAX_SIMULTANEOUS_SOUNDS = 32
    const val AUDIO_FALLOFF_DISTANCE = 50f

    // ==================== Performance ====================
    const val PARTICLE_POOL_SIZE = 1000
    const val PROJECTILE_POOL_SIZE = 500
    const val ENTITY_POOL_SIZE = 200
    const val MAX_PARTICLES = 2000
    const val LOD_DISTANCE_HIGH = 50f
    const val LOD_DISTANCE_MEDIUM = 100f
    const val LOD_DISTANCE_LOW = 200f

    // ==================== Time & Rewind ====================
    const val TIME_REWIND_DURATION = 5f // seconds
    const val TIME_REWIND_COOLDOWN = 30f // seconds
    const val TIME_SNAPSHOT_INTERVAL = 0.1f // seconds
    const val MAX_TIME_SNAPSHOTS = 50 // 5 seconds at 0.1s intervals

    // ==================== Abilities ====================
    const val MAX_ABILITY_SLOTS = 8
    const val SPELL_WEAVING_WINDOW = 2f // seconds
    const val MAX_SPELL_COMBO_LENGTH = 4
    const val MANA_REGEN_RATE = 10f // per second

    // ==================== Buffs & Debuffs ====================
    const val MAX_BUFFS = 10
    const val MAX_DEBUFFS = 10
    const val BUFF_TICK_RATE = 1f // seconds

    // ==================== AI ====================
    const val AI_UPDATE_INTERVAL = 0.1f // seconds
    const val AI_DETECTION_RANGE = 30f
    const val AI_ATTACK_RANGE = 2f
    const val AI_FLEE_HEALTH_THRESHOLD = 0.2f // 20%
    const val AI_WANDER_RADIUS = 20f

    // ==================== Collision Layers ====================
    const val LAYER_PLAYER = 1
    const val LAYER_ENEMY = 2
    const val LAYER_NPC = 4
    const val LAYER_PROJECTILE = 8
    const val LAYER_ENVIRONMENT = 16
    const val LAYER_TRIGGER = 32
    const val LAYER_ITEM = 64

    // ==================== Debug ====================
    const val DEBUG_MODE = true
    const val SHOW_FPS = true
    const val SHOW_HITBOXES = false
    const val SHOW_GRID = false
    const val SHOW_ENTITY_IDS = false
    const val LOG_NETWORK_PACKETS = false

    // ==================== File Paths ====================
    const val ASSETS_PATH = "assets/"
    const val MODELS_PATH = "models/"
    const val TEXTURES_PATH = "textures/"
    const val SHADERS_PATH = "shaders/"
    const val AUDIO_PATH = "audio/"
    const val DATA_PATH = "data/"
    const val SAVES_PATH = "saves/"

    // ==================== Database ====================
    const val DB_CONNECTION_TIMEOUT = 5000L // milliseconds
    const val DB_QUERY_TIMEOUT = 3000L // milliseconds
    const val DB_POOL_SIZE = 10

    // ==================== Session ====================
    const val SESSION_TIMEOUT = 3600000L // 1 hour in milliseconds
    const val AUTO_SAVE_INTERVAL = 300000L // 5 minutes in milliseconds
    const val HEARTBEAT_INTERVAL = 30000L // 30 seconds in milliseconds
}
