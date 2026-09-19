-- MicroCrew Seed Data: Demo Hackathons & Skills
-- For local development and demonstration testing.
-- Synthetic records only.

INSERT INTO public.hackathon (
    name, organizer, description, banner_url, registration_url,
    start_date, end_date, registration_deadline, location,
    mode, prize_pool, team_size_min, team_size_max, eligibility,
    difficulty, status
) VALUES
(
    'Global AI Innovation Sprint 2026',
    'OpenAI Dev Community & Cloud Foundation',
    'Build next-generation multimodal AI applications, autonomous agents, or LLM-augmented student tools. Open to students worldwide.',
    'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=1200&q=80',
    'https://example.com/hackathons/ai-innovation-sprint',
    NOW() + INTERVAL '20 days',
    NOW() + INTERVAL '22 days',
    NOW() + INTERVAL '15 days',
    'Online',
    'ONLINE',
    150000,
    2,
    4,
    'Undergraduate and graduate students with valid university ID',
    'INTERMEDIATE',
    'OPEN'
),
(
    'CyberShield Campus Defense Hackathon',
    'National Cybersecurity Alliance & TechCorp',
    'Develop active threat detection systems, secure identity protocols, and vulnerability analysis tools for university infrastructure.',
    'https://images.unsplash.com/photo-1550751827-4bd374c3f58b?w=1200&q=80',
    'https://example.com/hackathons/cybershield-campus',
    NOW() + INTERVAL '30 days',
    NOW() + INTERVAL '32 days',
    NOW() + INTERVAL '24 days',
    'Boston Tech Hub, MA',
    'HYBRID',
    100000,
    2,
    4,
    'College students enrolled in STEM or cybersecurity disciplines',
    'ADVANCED',
    'OPEN'
),
(
    'GreenTech IoT & Climate Solutionathon',
    'EcoTech Collective',
    'Use connected hardware, sensor telemetry, and analytics to solve campus energy waste and municipal climate challenges.',
    'https://images.unsplash.com/photo-1518770660439-4636190af475?w=1200&q=80',
    'https://example.com/hackathons/greentech-iot',
    NOW() + INTERVAL '45 days',
    NOW() + INTERVAL '47 days',
    NOW() + INTERVAL '38 days',
    'Austin Innovation Pavilion, TX',
    'OFFLINE',
    75000,
    3,
    5,
    'Any university students and recent grads (<1 year)',
    'INTERMEDIATE',
    'UPCOMING'
),
(
    'HealthHack: Clinical Software Sprint',
    'MedTech Future Labs',
    'Create intuitive patient portals, triage optimization tools, and medical data visualization dashboards for regional healthcare networks.',
    'https://images.unsplash.com/photo-1576091160399-112ba8d25d1d?w=1200&q=80',
    'https://example.com/hackathons/healthhack-clinical',
    NOW() + INTERVAL '14 days',
    NOW() + INTERVAL '16 days',
    NOW() + INTERVAL '10 days',
    'Online',
    'ONLINE',
    120000,
    1,
    4,
    'University students with interest in digital health',
    'BEGINNER',
    'OPEN'
),
(
    'Web3 Decentralized Identity Challenge',
    'OpenLedger Alliance',
    'Implement zero-knowledge credential verification and decentralized reputation systems for student academic achievements.',
    'https://images.unsplash.com/photo-1639762681485-074b7f938ba0?w=1200&q=80',
    'https://example.com/hackathons/web3-identity',
    NOW() + INTERVAL '60 days',
    NOW() + INTERVAL '62 days',
    NOW() + INTERVAL '50 days',
    'Online',
    'ONLINE',
    90000,
    2,
    4,
    'All enrolled students globally',
    'ADVANCED',
    'UPCOMING'
),
(
    'CloudScale Microservices Invitational',
    'Enterprise Systems Guild',
    'Architect fault-tolerant, containerized backend services capable of handling flash crowds and dynamic horizontal autoscaling.',
    'https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=1200&q=80',
    'https://example.com/hackathons/cloudscale-microservices',
    NOW() + INTERVAL '10 days',
    NOW() + INTERVAL '12 days',
    NOW() + INTERVAL '5 days',
    'Seattle Civic Center, WA',
    'HYBRID',
    80000,
    1,
    3,
    'Undergraduate computer science and engineering students',
    'INTERMEDIATE',
    'OPEN'
),
(
    'NextGen FinTech Mobile Buildathon',
    'Campus Capital & PayTech Group',
    'Design modern financial literacy micro-apps, peer-to-peer expense splitting, and budget forecast modules for student organizations.',
    'https://images.unsplash.com/photo-1559526324-4b87b5e36e44?w=1200&q=80',
    'https://example.com/hackathons/fintech-mobile',
    NOW() + INTERVAL '5 days',
    NOW() + INTERVAL '7 days',
    NOW() + INTERVAL '2 days',
    'Online',
    'ONLINE',
    50000,
    2,
    4,
    'Students worldwide (18+)',
    'BEGINNER',
    'OPEN'
),
(
    'EduCraft: Interactive Learning Hackathon',
    'Open Source Education Forum',
    'Build gamified learning widgets, collaborative markdown note-sharing, and interactive coding playground interfaces for STEM courses.',
    'https://images.unsplash.com/photo-1503676260728-1c00da094a0b?w=1200&q=80',
    'https://example.com/hackathons/educraft-interactive',
    NOW() + INTERVAL '25 days',
    NOW() + INTERVAL '27 days',
    NOW() + INTERVAL '18 days',
    'Online',
    'ONLINE',
    60000,
    1,
    4,
    'All students and self-taught developers enrolled in higher ed',
    'BEGINNER',
    'OPEN'
),
(
    'Autonomous Robotics & Edge Vision Derby',
    'RoboWorks Engineering Club',
    'Implement edge CV obstacle avoidance, SLAM navigation, and real-time sensor fusion algorithms for micro-rovers.',
    'https://images.unsplash.com/photo-1485827404703-89b55fcc595e?w=1200&q=80',
    'https://example.com/hackathons/robotics-derby',
    NOW() + INTERVAL '40 days',
    NOW() + INTERVAL '42 days',
    NOW() + INTERVAL '30 days',
    'Chicago Maker Park, IL',
    'OFFLINE',
    110000,
    3,
    5,
    'Engineering & Robotics students',
    'ADVANCED',
    'UPCOMING'
),
(
    'CivicData Open Innovation Sprint',
    'Metro Civic Tech Foundation',
    'Utilize public municipal open data APIs to build transit delays trackers, housing availability maps, and neighborhood service dashboards.',
    'https://images.unsplash.com/photo-1477959858617-67f30bc75b82?w=1200&q=80',
    'https://example.com/hackathons/civicdata-sprint',
    NOW() + INTERVAL '18 days',
    NOW() + INTERVAL '20 days',
    NOW() + INTERVAL '12 days',
    'Online',
    'ONLINE',
    40000,
    2,
    4,
    'College students interested in public sector technology',
    'BEGINNER',
    'OPEN'
);

-- Seed Hackathon Skills
-- Link skills to the inserted hackathons
INSERT INTO public.hackathon_skill (hackathon_id, skill_name) VALUES
-- 1: Global AI Innovation Sprint
(1, 'AI'), (1, 'Python'), (1, 'Machine Learning'), (1, 'React'),
-- 2: CyberShield
(2, 'Cybersecurity'), (2, 'Linux'), (2, 'Python'), (2, 'Network Security'),
-- 3: GreenTech IoT
(3, 'IoT'), (3, 'C++'), (3, 'Cloud'), (3, 'Embedded Systems'),
-- 4: HealthHack
(4, 'React'), (4, 'TypeScript'), (4, 'Node.js'), (4, 'UI/UX'),
-- 5: Web3 Identity
(5, 'Blockchain'), (5, 'Cryptography'), (5, 'Solidity'), (5, 'Rust'),
-- 6: CloudScale Microservices
(6, 'Java'), (6, 'Spring Boot'), (6, 'Docker'), (6, 'Kubernetes'),
-- 7: FinTech Mobile
(7, 'React Native'), (7, 'TypeScript'), (7, 'Mobile Development'), (7, 'API Integration'),
-- 8: EduCraft
(8, 'JavaScript'), (8, 'React'), (8, 'Tailwind CSS'), (8, 'UI/UX'),
-- 9: Robotics Derby
(9, 'Python'), (9, 'Computer Vision'), (9, 'ROS'), (9, 'C++'),
-- 10: CivicData
(10, 'Data Visualization'), (10, 'Python'), (10, 'SQL'), (10, 'Mapbox');
