import { createClient } from '@supabase/supabase-js'

const supabaseUrl = 'https://qxshcgkarlmnzbkiirgo.supabase.co'
const supabaseAnonKey = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InF4c2hjZ2thcmxtbnpia2lpcmdvIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjU4MDI4MjgsImV4cCI6MjA4MTM3ODgyOH0.oczuPkHe_N950RtcpbjZU8sXpe94bNp-Of5JbYJE82w'

export const supabase = createClient(supabaseUrl, supabaseAnonKey);

// Import the supabase client like this:
// For React:
// import { supabase } from "@/integrations/supabase/client";
// For React Native:
// import { supabase } from "@/src/integrations/supabase/client";
