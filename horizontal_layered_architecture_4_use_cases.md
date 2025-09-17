# Horizontal Layered Architecture - 4 Use Cases
## Navitaire: Passenger & Reservation | CAE: Crew & Flight

## Overall Horizontal Layered Architecture

```mermaid
graph TB
    subgraph "Layer 1: Source Systems"
        subgraph "Navitaire PSS"
            N1[Passenger Management<br/>Profile, Preferences, SSR]
            N2[Reservation System<br/>Booking, PNR, Payments]
        end
        
        subgraph "CAE Training"
            C1[Crew Management<br/>Qualifications, Scheduling]
            C2[Flight Operations<br/>Training, Simulation]
        end
    end
    
    subgraph "Layer 2: Integration Adapters"
        subgraph "Navitaire Adapters"
            NA1[REST API Adapter<br/>Real-time webhooks]
            NA2[CDC Adapter<br/>Database changes]
        end
        
        subgraph "CAE Adapters"
            CA1[SOAP Adapter<br/>Legacy integration]
            CA2[File Adapter<br/>Batch processing]
        end
    end
    
    subgraph "Layer 3: Azure Event Ingestion"
        EH1[Event Hub: Passenger Events<br/>4 partitions, 20 TU]
        EH2[Event Hub: Reservation Events<br/>4 partitions, 20 TU]
        EH3[Event Hub: Crew Events<br/>2 partitions, 10 TU]
        EH4[Event Hub: Flight Events<br/>2 partitions, 10 TU]
    end
    
    subgraph "Layer 4: Real-time Processing"
        SA1[Stream Analytics: Passenger<br/>24 SU, Pattern detection]
        SA2[Stream Analytics: Reservation<br/>24 SU, Aggregation]
        LA1[Logic Apps: Crew Workflow<br/>Multi-step processes]
        LA2[Logic Apps: Flight Workflow<br/>Training coordination]
    end
    
    subgraph "Layer 5: Dual Output Strategy"
        subgraph "Persistence Layer"
            CD1[Cosmos DB: Passenger Profiles<br/>Global distribution]
            CD2[Cosmos DB: Reservation Data<br/>Multi-region]
            SQL1[SQL DB: Crew Records<br/>Structured data]
            SQL2[SQL DB: Flight Training<br/>Compliance data]
        end
        
        subgraph "Real-time Layer"
            SR1[SignalR: Passenger Hub<br/>Live updates]
            SR2[SignalR: Reservation Hub<br/>Booking status]
            SR3[SignalR: Crew Hub<br/>Schedule updates]
            SR4[SignalR: Flight Hub<br/>Training status]
        end
        
        subgraph "Cache Layer"
            RC1[Redis: Hot Passenger Data<br/>< 1ms access]
            RC2[Redis: Active Reservations<br/>< 1ms access]
        end
    end
    
    subgraph "Layer 6: API Gateway"
        APIM[Azure API Management<br/>Unified gateway, OAuth 2.0]
    end
    
    subgraph "Layer 7: Consumer Applications"
        subgraph "Real-time Consumers"
            MOB1[Mobile Apps<br/>Passenger experience]
            WEB1[Web Portals<br/>Booking interface]
            CREW1[Crew Apps<br/>Schedule & training]
            OPS1[Operations Center<br/>Live monitoring]
        end
        
        subgraph "API Consumers"
            PART1[Partner Systems<br/>Travel agencies]
            ANLY1[Analytics Platform<br/>Business intelligence]
        end
    end
    
    N1 --> NA1
    N2 --> NA2
    C1 --> CA1
    C2 --> CA2
    
    NA1 --> EH1
    NA2 --> EH2
    CA1 --> EH3
    CA2 --> EH4
    
    EH1 --> SA1
    EH2 --> SA2
    EH3 --> LA1
    EH4 --> LA2
    
    SA1 --> CD1
    SA1 --> SR1
    SA1 --> RC1
    
    SA2 --> CD2
    SA2 --> SR2
    SA2 --> RC2
    
    LA1 --> SQL1
    LA1 --> SR3
    
    LA2 --> SQL2
    LA2 --> SR4
    
    CD1 --> APIM
    CD2 --> APIM
    SQL1 --> APIM
    SQL2 --> APIM
    RC1 --> APIM
    RC2 --> APIM
    
    SR1 --> MOB1
    SR2 --> WEB1
    SR3 --> CREW1
    SR4 --> OPS1
    
    APIM --> PART1
    APIM --> ANLY1
```

## Use Case 1: Navitaire Passenger Flow

### Passenger Profile Management Data Flow
```mermaid
graph TB
    subgraph "Passenger Source Events"
        PE1[Profile Created<br/>New customer registration]
        PE2[Profile Updated<br/>Contact, preferences]
        PE3[SSR Added<br/>Special service requests]
        PE4[Loyalty Status<br/>Tier changes, points]
    end
    
    subgraph "Integration Layer"
        API1[REST API Webhook<br/>Real-time notifications]
        CDC1[Database CDC<br/>Profile table changes]
        MQ1[Message Queue<br/>Loyalty system events]
    end
    
    subgraph "Event Hub Processing"
        EH1[Passenger Events Hub<br/>Partition by customer ID]
        EH1A[Partition 0: A-F customers]
        EH1B[Partition 1: G-M customers]
        EH1C[Partition 2: N-S customers]
        EH1D[Partition 3: T-Z customers]
    end
    
    subgraph "Stream Analytics Processing"
        SA1[Passenger Stream Analytics<br/>24 SU, Auto-scale]
        SA1A[Profile Validation<br/>Data quality checks]
        SA1B[Preference Analysis<br/>Behavior patterns]
        SA1C[Loyalty Calculation<br/>Points aggregation]
    end
    
    subgraph "Dual Output Destinations"
        subgraph "Persistence"
            CD1[Cosmos DB: Passenger Collection<br/>Partition: /customerId]
            RC1[Redis Cache: Active Profiles<br/>TTL: 24 hours]
        end
        
        subgraph "Real-time"
            SR1[SignalR: Passenger Hub<br/>Connection groups by region]
            EH2[Event Hub: Processed Events<br/>Downstream distribution]
        end
    end
    
    subgraph "Consumer Applications"
        subgraph "Immediate Updates (< 100ms)"
            MOB1[Mobile App<br/>Profile sync]
            WEB1[Web Portal<br/>Account dashboard]
            CS1[Customer Service<br/>Agent console]
        end
        
        subgraph "API Queries (< 500ms)"
            PART1[Travel Partners<br/>Profile lookup]
            ANLY1[Analytics<br/>Customer insights]
        end
    end
    
    PE1 --> API1
    PE2 --> CDC1
    PE3 --> API1
    PE4 --> MQ1
    
    API1 --> EH1A
    CDC1 --> EH1B
    MQ1 --> EH1C
    
    EH1A --> SA1A
    EH1B --> SA1B
    EH1C --> SA1C
    
    SA1A --> CD1
    SA1A --> RC1
    SA1A --> SR1
    SA1A --> EH2
    
    SA1B --> CD1
    SA1B --> RC1
    SA1B --> SR1
    
    SA1C --> CD1
    SA1C --> RC1
    SA1C --> SR1
    
    SR1 --> MOB1
    SR1 --> WEB1
    SR1 --> CS1
    
    EH2 --> AF1[Azure Functions<br/>API processors]
    AF1 --> PART1
    AF1 --> ANLY1
    
    CD1 --> API2[Query APIs]
    RC1 --> API2
    API2 --> PART1
    API2 --> ANLY1
```

### Passenger Event Processing Sequence
```mermaid
sequenceDiagram
    participant N as Navitaire PSS
    participant API as REST API Adapter
    participant EH as Event Hub
    participant SA as Stream Analytics
    participant CD as Cosmos DB
    participant RC as Redis Cache
    participant SR as SignalR Hub
    participant MA as Mobile App
    participant WP as Web Portal
    
    Note over N,WP: Passenger Profile Update Scenario
    
    N->>API: Profile Updated Event
    Note right of N: Customer ID: 12345<br/>Field: Email address<br/>Timestamp: T0
    
    API->>EH: Event Ingestion
    Note right of API: Partition: Hash(12345)<br/>Latency: < 10ms<br/>Timestamp: T0+10ms
    
    EH->>SA: Stream Processing
    Note right of EH: Validation & enrichment<br/>Timestamp: T0+20ms
    
    par Dual Output Processing
        SA->>CD: Store Profile Data
        Note right of SA: Global write<br/>Consistency: Strong<br/>Timestamp: T0+50ms
        
        SA->>RC: Cache Hot Data
        Note right of SA: TTL: 24 hours<br/>Timestamp: T0+30ms
        
        SA->>SR: Real-time Update
        Note right of SA: Connection group: Region<br/>Timestamp: T0+40ms
    end
    
    par Consumer Notifications
        SR->>MA: WebSocket Push
        Note right of SR: Profile sync<br/>Timestamp: T0+50ms
        
        SR->>WP: WebSocket Push
        Note right of SR: Dashboard update<br/>Timestamp: T0+55ms
    end
    
    Note over N,WP: Total End-to-End Latency: < 100ms
```

## Use Case 2: Navitaire Reservation Flow

### Reservation Booking Data Flow
```mermaid
graph TB
    subgraph "Reservation Source Events"
        RE1[Booking Created<br/>New reservation]
        RE2[Payment Processed<br/>Transaction complete]
        RE3[Seat Selected<br/>Inventory update]
        RE4[Booking Modified<br/>Changes, cancellations]
    end
    
    subgraph "Integration Layer"
        API2[Booking API<br/>Real-time webhooks]
        CDC2[Payment CDC<br/>Transaction table]
        INV1[Inventory API<br/>Seat management]
    end
    
    subgraph "Event Hub Processing"
        EH2[Reservation Events Hub<br/>Partition by flight number]
        EH2A[Partition 0: Domestic flights]
        EH2B[Partition 1: International flights]
        EH2C[Partition 2: Charter flights]
        EH2D[Partition 3: Codeshare flights]
    end
    
    subgraph "Stream Analytics Processing"
        SA2[Reservation Stream Analytics<br/>24 SU, Complex queries]
        SA2A[Booking Validation<br/>Business rules]
        SA2B[Revenue Calculation<br/>Fare aggregation]
        SA2C[Inventory Update<br/>Seat availability]
    end
    
    subgraph "Dual Output Destinations"
        subgraph "Persistence"
            CD2[Cosmos DB: Reservation Collection<br/>Partition: /flightNumber]
            RC2[Redis Cache: Active Bookings<br/>TTL: 48 hours]
            SQL1[SQL DB: Financial Records<br/>ACID compliance]
        end
        
        subgraph "Real-time"
            SR2[SignalR: Reservation Hub<br/>Flight-based groups]
            EH3[Event Hub: Booking Events<br/>Partner distribution]
        end
    end
    
    subgraph "Consumer Applications"
        subgraph "Immediate Updates (< 100ms)"
            WEB2[Booking Website<br/>Seat map updates]
            MOB2[Mobile App<br/>Booking status]
            OPS2[Operations Center<br/>Load factor]
        end
        
        subgraph "API Queries (< 500ms)"
            AIRP1[Airport Systems<br/>Check-in status]
            PART2[Travel Agencies<br/>Booking lookup]
        end
    end
    
    RE1 --> API2
    RE2 --> CDC2
    RE3 --> INV1
    RE4 --> API2
    
    API2 --> EH2A
    CDC2 --> EH2B
    INV1 --> EH2C
    
    EH2A --> SA2A
    EH2B --> SA2B
    EH2C --> SA2C
    
    SA2A --> CD2
    SA2A --> RC2
    SA2A --> SR2
    SA2A --> EH3
    
    SA2B --> SQL1
    SA2B --> SR2
    
    SA2C --> CD2
    SA2C --> RC2
    SA2C --> SR2
    
    SR2 --> WEB2
    SR2 --> MOB2
    SR2 --> OPS2
    
    EH3 --> AF2[Azure Functions<br/>Partner APIs]
    AF2 --> AIRP1
    AF2 --> PART2
    
    CD2 --> API3[Booking APIs]
    RC2 --> API3
    SQL1 --> API3
    API3 --> AIRP1
    API3 --> PART2
```

### Reservation Booking Sequence
```mermaid
sequenceDiagram
    participant N as Navitaire PSS
    participant API as Booking API
    participant EH as Event Hub
    participant SA as Stream Analytics
    participant CD as Cosmos DB
    participant RC as Redis Cache
    participant SQL as SQL Database
    participant SR as SignalR Hub
    participant WEB as Booking Website
    participant OPS as Operations Center
    
    Note over N,OPS: Flight Booking Scenario
    
    N->>API: Booking Created Event
    Note right of N: Flight: F9123<br/>Passenger: John Doe<br/>Seat: 12A<br/>Timestamp: T0
    
    API->>EH: Event Ingestion
    Note right of API: Partition: Hash(F9123)<br/>Timestamp: T0+5ms
    
    EH->>SA: Stream Processing
    Note right of EH: Booking validation<br/>Revenue calculation<br/>Timestamp: T0+15ms
    
    par Triple Output Processing
        SA->>CD: Store Booking Data
        Note right of SA: Reservation collection<br/>Timestamp: T0+40ms
        
        SA->>RC: Cache Active Booking
        Note right of SA: TTL: 48 hours<br/>Timestamp: T0+25ms
        
        SA->>SQL: Financial Record
        Note right of SA: ACID transaction<br/>Timestamp: T0+60ms
        
        SA->>SR: Real-time Update
        Note right of SA: Flight group: F9123<br/>Timestamp: T0+35ms
    end
    
    par Consumer Notifications
        SR->>WEB: Seat Map Update
        Note right of SR: Seat 12A unavailable<br/>Timestamp: T0+45ms
        
        SR->>OPS: Load Factor Update
        Note right of SR: Flight capacity: 89%<br/>Timestamp: T0+50ms
    end
    
    Note over N,OPS: Total End-to-End Latency: < 100ms
```

## Use Case 3: CAE Crew Flow

### Crew Management Data Flow
```mermaid
graph TB
    subgraph "Crew Source Events"
        CE1[Crew Certification<br/>Training completed]
        CE2[Medical Renewal<br/>Health clearance]
        CE3[Schedule Assignment<br/>Flight duty]
        CE4[Availability Change<br/>Personal, sick leave]
    end
    
    subgraph "Integration Layer"
        SOAP1[SOAP Adapter<br/>Legacy crew system]
        FILE1[File Adapter<br/>Certification files]
        DB1[Database Adapter<br/>Direct SQL access]
    end
    
    subgraph "Event Hub Processing"
        EH3[Crew Events Hub<br/>Partition by crew base]
        EH3A[Partition 0: Hub A crew]
        EH3B[Partition 1: Hub B crew]
    end
    
    subgraph "Logic Apps Workflow"
        LA1[Crew Workflow Engine<br/>Multi-step processes]
        LA1A[Certification Validation<br/>Requirements check]
        LA1B[Schedule Optimization<br/>Duty time compliance]
        LA1C[Notification Routing<br/>Alert distribution]
    end
    
    subgraph "Data Storage"
        SQL2[SQL Database: Crew Records<br/>Structured, ACID]
        SQL2A[Crew_Profiles table]
        SQL2B[Certifications table]
        SQL2C[Schedules table]
    end
    
    subgraph "Real-time Distribution"
        SR3[SignalR: Crew Hub<br/>Base-specific groups]
        SB1[Service Bus: Workflow Queue<br/>Reliable messaging]
        NH1[Notification Hubs<br/>Mobile push]
    end
    
    subgraph "Consumer Applications"
        subgraph "Immediate Updates (< 1s)"
            CREW2[Crew Mobile App<br/>Schedule updates]
            SCHED1[Crew Scheduling<br/>Roster management]
            OPS3[Operations Center<br/>Crew availability]
        end
        
        subgraph "Workflow Updates (minutes)"
            PAYROLL1[Payroll System<br/>Duty hours]
            COMP1[Compliance System<br/>Regulatory check]
        end
    end
    
    CE1 --> SOAP1
    CE2 --> FILE1
    CE3 --> DB1
    CE4 --> SOAP1
    
    SOAP1 --> EH3A
    FILE1 --> EH3A
    DB1 --> EH3B
    
    EH3A --> LA1A
    EH3B --> LA1B
    
    LA1A --> SQL2A
    LA1A --> SQL2B
    LA1A --> SR3
    LA1A --> SB1
    
    LA1B --> SQL2C
    LA1B --> SR3
    LA1B --> NH1
    
    LA1C --> SR3
    LA1C --> NH1
    
    SR3 --> CREW2
    SR3 --> SCHED1
    SR3 --> OPS3
    
    SB1 --> WF1[Workflow Triggers]
    WF1 --> PAYROLL1
    WF1 --> COMP1
    
    NH1 --> CREW2
```

### Crew Certification Sequence
```mermaid
sequenceDiagram
    participant C as CAE Training
    participant SOAP as SOAP Adapter
    participant EH as Event Hub
    participant LA as Logic Apps
    participant SQL as SQL Database
    participant SR as SignalR Hub
    participant SB as Service Bus
    participant CREW as Crew App
    participant SCHED as Crew Scheduling
    
    Note over C,SCHED: Crew Certification Scenario
    
    C->>SOAP: Training Completed
    Note right of C: Crew ID: 5678<br/>Course: B737 Type Rating<br/>Score: 95%<br/>Timestamp: T0
    
    SOAP->>EH: Event Ingestion
    Note right of SOAP: Crew base partition<br/>Timestamp: T0+30s
    
    EH->>LA: Workflow Trigger
    Note right of EH: Certification workflow<br/>Timestamp: T0+45s
    
    LA->>LA: Validation Process
    Note right of LA: Check prerequisites<br/>Medical validity<br/>Experience requirements<br/>Duration: 2-5 minutes
    
    par Workflow Completion
        LA->>SQL: Update Certification
        Note right of LA: Crew_Certifications table<br/>Active status<br/>Timestamp: T0+5min
        
        LA->>SR: Real-time Notification
        Note right of LA: Crew hub group<br/>Timestamp: T0+5min
        
        LA->>SB: Workflow Queue
        Note right of LA: Downstream processes<br/>Timestamp: T0+5min
    end
    
    par Consumer Updates
        SR->>CREW: Certification Alert
        Note right of SR: Mobile notification<br/>New qualification<br/>Timestamp: T0+5min
        
        SR->>SCHED: Crew Available
        Note right of SR: Scheduling update<br/>B737 qualified<br/>Timestamp: T0+5min
    end
    
    SB->>PAYROLL1[Payroll System]
    Note right of SB: Qualification pay<br/>Effective date<br/>Timestamp: T0+10min
    
    Note over C,SCHED: Total Process Time: 5-10 minutes<br/>Real-time notifications: < 1 second
```

## Use Case 4: CAE Flight Flow

### Flight Training Data Flow
```mermaid
graph TB
    subgraph "Flight Training Events"
        FE1[Simulator Session<br/>Training progress]
        FE2[Check Ride<br/>Proficiency test]
        FE3[Recurrent Training<br/>Annual requirements]
        FE4[Emergency Procedures<br/>Safety training]
    end
    
    subgraph "Integration Layer"
        SIM1[Simulator Interface<br/>Real-time data feed]
        FILE2[Training Records<br/>Batch file processing]
        API3[Training API<br/>REST endpoints]
    end
    
    subgraph "Event Hub Processing"
        EH4[Flight Events Hub<br/>Partition by aircraft type]
        EH4A[Partition 0: B737 training]
        EH4B[Partition 1: A320 training]
    end
    
    subgraph "Logic Apps Workflow"
        LA2[Flight Training Workflow<br/>Complex orchestration]
        LA2A[Progress Tracking<br/>Milestone validation]
        LA2B[Compliance Check<br/>Regulatory requirements]
        LA2C[Scheduling Coordination<br/>Resource allocation]
    end
    
    subgraph "Data Storage"
        SQL3[SQL Database: Training Records<br/>Compliance data]
        SQL3A[Training_Sessions table]
        SQL3B[Proficiency_Checks table]
        SQL3C[Compliance_Status table]
    end
    
    subgraph "Real-time Distribution"
        SR4[SignalR: Flight Training Hub<br/>Aircraft type groups]
        EG1[Event Grid: Training Events<br/>Custom topics]
        NH2[Notification Hubs<br/>Training alerts]
    end
    
    subgraph "Consumer Applications"
        subgraph "Real-time Updates (< 1s)"
            INST1[Instructor Dashboard<br/>Progress monitoring]
            CREW3[Crew Training App<br/>Personal progress]
            OPS4[Training Operations<br/>Resource utilization]
        end
        
        subgraph "Compliance Updates (hours)"
            REG1[Regulatory System<br/>Compliance reporting]
            QUAL1[Qualification Tracking<br/>Currency status]
        end
    end
    
    FE1 --> SIM1
    FE2 --> API3
    FE3 --> FILE2
    FE4 --> SIM1
    
    SIM1 --> EH4A
    API3 --> EH4A
    FILE2 --> EH4B
    
    EH4A --> LA2A
    EH4B --> LA2B
    
    LA2A --> SQL3A
    LA2A --> SR4
    LA2A --> EG1
    
    LA2B --> SQL3B
    LA2B --> SQL3C
    LA2B --> SR4
    LA2B --> NH2
    
    LA2C --> SR4
    LA2C --> EG1
    
    SR4 --> INST1
    SR4 --> CREW3
    SR4 --> OPS4
    
    EG1 --> CT1[Custom Topics]
    CT1 --> REG1
    CT1 --> QUAL1
    
    NH2 --> CREW3
```

### Flight Training Sequence
```mermaid
sequenceDiagram
    participant C as CAE Simulator
    participant SIM as Simulator Interface
    participant EH as Event Hub
    participant LA as Logic Apps
    participant SQL as SQL Database
    participant SR as SignalR Hub
    participant EG as Event Grid
    participant INST as Instructor Dashboard
    participant CREW as Crew App
    
    Note over C,CREW: Simulator Training Session
    
    C->>SIM: Session Started
    Note right of C: Crew ID: 5678<br/>Aircraft: B737-800<br/>Scenario: Emergency Landing<br/>Timestamp: T0
    
    SIM->>EH: Real-time Data Feed
    Note right of SIM: Performance metrics<br/>Every 30 seconds<br/>Timestamp: T0 to T0+2hrs
    
    loop Training Session (2 hours)
        EH->>LA: Progress Update
        Note right of EH: Performance data<br/>Milestone tracking
        
        LA->>SQL: Store Session Data
        Note right of LA: Training_Sessions table<br/>Real-time updates
        
        LA->>SR: Live Progress
        Note right of LA: Instructor monitoring<br/>< 1 second latency
        
        SR->>INST: Dashboard Update
        Note right of SR: Performance graphs<br/>Real-time metrics
    end
    
    C->>SIM: Session Completed
    Note right of C: Final score: 88%<br/>Status: Passed<br/>Timestamp: T0+2hrs
    
    SIM->>EH: Final Results
    Note right of SIM: Complete session data<br/>Timestamp: T0+2hrs
    
    EH->>LA: Completion Workflow
    Note right of EH: Final processing<br/>Timestamp: T0+2hrs+30s
    
    par Completion Processing
        LA->>SQL: Final Record
        Note right of LA: Proficiency_Checks table<br/>Certification update<br/>Timestamp: T0+2hrs+1min
        
        LA->>SR: Completion Alert
        Note right of LA: Training complete<br/>Timestamp: T0+2hrs+45s
        
        LA->>EG: Compliance Event
        Note right of LA: Regulatory notification<br/>Timestamp: T0+2hrs+1min
    end
    
    par Consumer Notifications
        SR->>CREW: Training Complete
        Note right of SR: Mobile notification<br/>Certificate available<br/>Timestamp: T0+2hrs+1min
        
        EG->>REG1[Regulatory System]
        Note right of EG: Compliance update<br/>Training record<br/>Timestamp: T0+2hrs+2min
    end
    
    Note over C,CREW: Session Duration: 2 hours<br/>Real-time updates: < 1 second<br/>Final processing: < 2 minutes
```

## Performance Specifications by Use Case

### Latency and Throughput Matrix
```mermaid
graph TB
    subgraph "Performance Targets"
        subgraph "Navitaire Use Cases"
            N1P[Passenger Flow<br/>Target: < 100ms<br/>Volume: 30K events/hour<br/>Peak: 50K events/hour]
            N2P[Reservation Flow<br/>Target: < 100ms<br/>Volume: 20K events/hour<br/>Peak: 35K events/hour]
        end
        
        subgraph "CAE Use Cases"
            C1P[Crew Flow<br/>Target: < 5 minutes<br/>Volume: 1K events/hour<br/>Peak: 2K events/hour]
            C2P[Flight Training Flow<br/>Target: < 1 second<br/>Volume: 500 events/hour<br/>Peak: 1K events/hour]
        end
        
        subgraph "Azure Resource Allocation"
            EH_RES[Event Hub<br/>Navitaire: 40 TU<br/>CAE: 20 TU]
            SA_RES[Stream Analytics<br/>Navitaire: 48 SU<br/>CAE: N/A]
            LA_RES[Logic Apps<br/>Navitaire: N/A<br/>CAE: Standard Plan]
            DB_RES[Databases<br/>Cosmos DB: 20K RU/s<br/>SQL DB: S3 Standard]
        end
    end
    
    N1P --> EH_RES
    N2P --> SA_RES
    C1P --> LA_RES
    C2P --> DB_RES
```

## Cross-Use Case Integration Points

### Inter-System Event Correlation
```mermaid
graph TB
    subgraph "Correlation Scenarios"
        subgraph "Passenger-Crew Correlation"
            PC1[VIP Passenger Booking<br/>→ Special Crew Assignment]
            PC2[Flight Disruption<br/>→ Crew Reallocation + Passenger Rebooking]
        end
        
        subgraph "Reservation-Training Correlation"
            RT1[New Route Launch<br/>→ Crew Training Requirements]
            RT2[Aircraft Change<br/>→ Crew Requalification + Booking Updates]
        end
    end
    
    subgraph "Correlation Processing"
        EH_CORR[Event Hub: Correlation Events<br/>Cross-system partition]
        SA_CORR[Stream Analytics: Pattern Detection<br/>Complex event processing]
        LA_CORR[Logic Apps: Cross-system Workflow<br/>Multi-step orchestration]
    end
    
    subgraph "Unified Consumer Updates"
        SR_UNIFIED[SignalR: Unified Hub<br/>Cross-system notifications]
        DASH_UNIFIED[Operations Dashboard<br/>360-degree view]
    end
    
    PC1 --> EH_CORR
    PC2 --> EH_CORR
    RT1 --> EH_CORR
    RT2 --> EH_CORR
    
    EH_CORR --> SA_CORR
    EH_CORR --> LA_CORR
    
    SA_CORR --> SR_UNIFIED
    LA_CORR --> SR_UNIFIED
    
    SR_UNIFIED --> DASH_UNIFIED
```
