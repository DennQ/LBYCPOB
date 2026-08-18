-- ============================================================
-- PROFILES TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS public.profiles (
                                               id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name TEXT NOT NULL,
    status TEXT DEFAULT '' NOT NULL,
    quote TEXT DEFAULT '' NOT NULL,
    picture TEXT DEFAULT '' NOT NULL,
    student_id TEXT,
    course TEXT,
    year_level INTEGER,
    created_at TIMESTAMPTZ DEFAULT now() NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT now() NOT NULL
    );

-- ============================================================
-- ORGANIZATIONS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS public.organizations (
                                                    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    category TEXT,
    email TEXT,
    logo_url TEXT DEFAULT '',
    created_at TIMESTAMPTZ DEFAULT now() NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT now() NOT NULL
    );

-- ============================================================
-- EVENTS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS public.events (
                                             id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    name TEXT NOT NULL,
    description TEXT,
    venue TEXT,
    event_date TIMESTAMPTZ NOT NULL,
    capacity INTEGER DEFAULT 0,
    created_at TIMESTAMPTZ DEFAULT now() NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT now() NOT NULL
    );

-- ============================================================
-- EVENT REGISTRATIONS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS public.event_registrations (
                                                          id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    event_id UUID NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    profile_id UUID NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
    registration_date TIMESTAMPTZ DEFAULT now() NOT NULL,
    status TEXT DEFAULT 'registered',
    created_at TIMESTAMPTZ DEFAULT now() NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT now() NOT NULL,
    UNIQUE(event_id, profile_id)
    );

-- ============================================================
-- ORGANIZATION MEMBERS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS public.organization_members (
                                                           id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    profile_id UUID NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
    role TEXT DEFAULT 'member',
    created_at TIMESTAMPTZ DEFAULT now() NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT now() NOT NULL,
    UNIQUE(organization_id, profile_id)
    );

-- ============================================================
-- FRIENDS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS public.friends (
                                              id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    profile_id UUID NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
    friend_id UUID NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ DEFAULT now() NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT now() NOT NULL,
    UNIQUE(profile_id, friend_id)
    );

-- ============================================================
-- INDEXES
-- ============================================================
CREATE INDEX IF NOT EXISTS idx_events_organization_id ON public.events(organization_id);
CREATE INDEX IF NOT EXISTS idx_events_event_date ON public.events(event_date);
CREATE INDEX IF NOT EXISTS idx_registrations_event_id ON public.event_registrations(event_id);
CREATE INDEX IF NOT EXISTS idx_registrations_profile_id ON public.event_registrations(profile_id);
CREATE INDEX IF NOT EXISTS idx_org_members_org ON public.organization_members(organization_id);
CREATE INDEX IF NOT EXISTS idx_org_members_profile ON public.organization_members(profile_id);
CREATE INDEX IF NOT EXISTS idx_friends_profile_id ON public.friends(profile_id);
CREATE INDEX IF NOT EXISTS idx_friends_friend_id ON public.friends(friend_id);