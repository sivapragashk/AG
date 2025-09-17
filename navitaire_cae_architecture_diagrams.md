# Navitaire & CAE Event Platform Architecture - Diagrams Only

## High-Level System Architecture

### Overall System Overview
```mermaid
graph TB
    subgraph "Source Systems"
        N[Navitaire PSS<br/>Passenger & Reservations<br/>50K events/hour]
        C[CAE Training<br/>Crew & Operations<br/>2K events/hour]
    end
    
    subgraph "Microsoft Azure Event Platform"
        subgraph "Ingestion Layer"
            EH[Azure Event Hub<br/>Event Ingestion]
        end
        
        subgraph "Processing Layer"
            SA[Azure Stream Analytics<br/>Real-time Processing]
        end
        
        subgraph "Dual Output Strategy"
            subgraph "Data Storage"
                DB[(Azure Cosmos DB<br/>Operational Data)]
                CACHE[(Redis Cache<br/>Hot Data)]
            end
            
            subgraph "Real-time Delivery"
                SR[SignalR Service<br/>Live Updates]
                EH2[Event Hub<br/>Event Distribution]
            end
        end
        
        subgraph "API Gateway"
            APIM[Azure API Management<br/>Secure Gateway]
        end
    end
    
    subgraph "Consumer Applications"
        subgraph "Real-time Consumers"
            MOBILE[Mobile Apps<br/>Live Updates]
            WEB[Web Portals<br/>Live Dashboard]
            OPS[Operations Center<br/>Real-time Monitoring]
            CREW[Crew Applications<br/>Live Scheduling]
        end
        
        subgraph "API Consumers"
            PARTNER[Partner Systems<br/>REST APIs]
            ANALYTICS[Analytics Tools<br/>Data Queries]
        end
    end
    
    N --> EH
    C --> EH
    
    EH --> SA
    
    SA --> DB
    SA --> CACHE
    SA --> SR
    SA --> EH2
    
    DB --> APIM
    CACHE --> APIM
    
    SR --> MOBILE
    SR --> WEB
    SR --> OPS
    SR --> CREW
    
    EH2 --> MOBILE
    EH2 --> WEB
    EH2 --> OPS
    EH2 --> CREW
    
    APIM --> PARTNER
    APIM --> ANALYTICS
```

## Detailed Architecture - Navitaire PSS Integration

### Navitaire Source System Integration
```mermaid
graph TB
    subgraph "Navitaire PSS System"
        NPS[Passenger Service System]
        NRS[Reservation System]
        NIM[Inventory Management]
        NLP[Loyalty Programs]
        NRM[Revenue Management]
    end
    
    subgraph "Integration Methods"
        subgraph "Real-time APIs"
            API1[REST API Webhooks<br/>< 100ms latency]
            API2[GraphQL Subscriptions<br/>Real-time queries]
        end
        
        subgraph "Database Integration"
            CDC1[Change Data Capture<br/>SQL Server CDC]
            TRG1[Database Triggers<br/>Immediate events]
        end
        
        subgraph "Message Queues"
            MQ1[IBM MQ Integration<br/>Reliable messaging]
            MQ2[RabbitMQ Adapter<br/>Event streaming]
        end
    end
    
    subgraph "Event Types"
        subgraph "Passenger Events"
            PE1[PNR Created/Updated]
            PE2[Passenger Profile Changes]
            PE3[Special Service Requests]
        end
        
        subgraph "Booking Events"
            BE1[Reservation Created]
            BE2[Payment Processing]
            BE3[Booking Modifications]
        end
        
        subgraph "Inventory Events"
            IE1[Seat Availability]
            IE2[Fare Changes]
            IE3[Class Upgrades]
        end
        
        subgraph "Loyalty Events"
            LE1[Points Earned/Redeemed]
            LE2[Tier Status Changes]
            LE3[Promotional Offers]
        end
    end
    
    NPS --> API1
    NRS --> CDC1
    NIM --> MQ1
    NLP --> API2
    NRM --> TRG1
    
    API1 --> PE1
    API1 --> BE1
    CDC1 --> PE2
    CDC1 --> BE2
    MQ1 --> IE1
    MQ1 --> IE2
    API2 --> LE1
    TRG1 --> IE3
```

### Navitaire Event Processing Flow
```mermaid
graph TB
    subgraph "Navitaire Events"
        NE1[PNR Events<br/>Bookings, Changes]
        NE2[Inventory Events<br/>Seat Availability]
        NE3[Loyalty Events<br/>Points, Tiers]
        NE4[Payment Events<br/>Transactions]
    end
    
    subgraph "Azure Event Hub"
        EH1[Passenger Events Hub<br/>4 partitions, 20 TU]
        EH2[Flight Events Hub<br/>4 partitions, 20 TU]
    end
    
    subgraph "Stream Analytics Processing"
        SA1[Passenger Processing<br/>24 SU, SQL queries]
        SA2[Flight Processing<br/>24 SU, Pattern detection]
    end
    
    subgraph "Dual Output Destinations"
        subgraph "Persistence"
            CD1[Cosmos DB<br/>Passenger Profiles]
            CD2[Cosmos DB<br/>Flight Data]
            RC1[Redis Cache<br/>Hot Inventory]
        end
        
        subgraph "Real-time"
            SR1[SignalR<br/>Passenger Hub]
            SR2[SignalR<br/>Flight Hub]
            EH3[Event Hub<br/>Processed Events]
        end
    end
    
    NE1 --> EH1
    NE2 --> EH2
    NE3 --> EH1
    NE4 --> EH1
    
    EH1 --> SA1
    EH2 --> SA2
    
    SA1 --> CD1
    SA1 --> RC1
    SA1 --> SR1
    SA1 --> EH3
    
    SA2 --> CD2
    SA2 --> RC1
    SA2 --> SR2
    SA2 --> EH3
```

## Detailed Architecture - CAE Training Integration

### CAE Source System Integration
```mermaid
graph TB
    subgraph "CAE Training System"
        CTS[Training Management System]
        CCS[Crew Certification System]
        CFS[Flight Simulation System]
        CMS[Crew Management System]
    end
    
    subgraph "Integration Methods"
        subgraph "Legacy Integration"
            SOAP1[SOAP Web Services<br/>XML messaging]
            FILE1[File-based Integration<br/>CSV/XML files]
        end
        
        subgraph "Modern Integration"
            REST1[REST API Wrapper<br/>JSON conversion]
            DB1[Database Integration<br/>Oracle/SQL Server]
        end
    end
    
    subgraph "Event Types"
        subgraph "Training Events"
            TE1[Training Completed]
            TE2[Course Enrollment]
            TE3[Exam Results]
        end
        
        subgraph "Crew Events"
            CE1[Crew Certification]
            CE2[Medical Renewals]
            CE3[Duty Time Updates]
        end
        
        subgraph "Operational Events"
            OE1[Flight Assignment]
            OE2[Emergency Replacement]
            OE3[Compliance Alerts]
        end
    end
    
    CTS --> SOAP1
    CCS --> DB1
    CFS --> FILE1
    CMS --> REST1
    
    SOAP1 --> TE1
    DB1 --> CE1
    FILE1 --> TE2
    REST1 --> OE1
```

### CAE Event Processing Flow
```mermaid
graph TB
    subgraph "CAE Events"
        CE1[Training Events<br/>Certifications, Courses]
        CE2[Crew Events<br/>Qualifications, Medical]
        CE3[Operational Events<br/>Assignments, Alerts]
    end
    
    subgraph "Azure Integration"
        EH1[Event Hub<br/>Crew Events, 2 partitions]
        SA1[Stream Analytics<br/>Crew Processing, 12 SU]
    end
    
    subgraph "Workflow Processing"
        LA1[Logic Apps<br/>Certification Workflow]
        LA2[Logic Apps<br/>Crew Assignment Workflow]
        SB1[Service Bus<br/>Workflow Queue]
    end
    
    subgraph "Data Storage"
        SQL1[SQL Database<br/>Crew Records]
        CD1[Cosmos DB<br/>Training Data]
    end
    
    subgraph "Real-time Delivery"
        SR1[SignalR<br/>Crew Hub]
        NH1[Notification Hubs<br/>Mobile Alerts]
    end
    
    CE1 --> EH1
    CE2 --> EH1
    CE3 --> EH1
    
    EH1 --> SA1
    
    SA1 --> SQL1
    SA1 --> CD1
    SA1 --> SB1
    SA1 --> SR1
    
    SB1 --> LA1
    SB1 --> LA2
    
    LA1 --> NH1
    LA2 --> SR1
```

## Modular Data Flow Diagrams

### End-to-End Navitaire Booking Flow
```mermaid
sequenceDiagram
    participant N as Navitaire PSS
    participant EH as Event Hub
    participant SA as Stream Analytics
    participant CD as Cosmos DB
    participant RC as Redis Cache
    participant SR as SignalR Service
    participant MA as Mobile App
    participant WP as Web Portal
    
    Note over N,WP: Passenger Books Flight Scenario
    
    N->>EH: PNR Created Event
    Note right of N: Event: PNR_CREATED<br/>Timestamp: T0
    
    EH->>SA: Event Ingestion
    Note right of EH: Latency: < 10ms<br/>Timestamp: T0+10ms
    
    par Dual Output Processing
        SA->>CD: Store PNR Data
        Note right of SA: Persistence Path<br/>Timestamp: T0+50ms
        
        SA->>RC: Cache Hot Data
        Note right of SA: Cache Path<br/>Timestamp: T0+30ms
        
        SA->>SR: Real-time Update
        Note right of SA: Push Path<br/>Timestamp: T0+40ms
    end
    
    par Real-time Consumer Updates
        SR->>MA: WebSocket Push
        Note right of SR: Mobile Update<br/>Timestamp: T0+50ms
        
        SR->>WP: WebSocket Push
        Note right of SR: Web Update<br/>Timestamp: T0+55ms
    end
    
    Note over N,WP: Total End-to-End Latency: < 100ms
```

### End-to-End CAE Crew Certification Flow
```mermaid
sequenceDiagram
    participant C as CAE Training
    participant EH as Event Hub
    participant SA as Stream Analytics
    participant SB as Service Bus
    participant LA as Logic Apps
    participant SQL as SQL Database
    participant SR as SignalR Service
    participant CS as Crew Scheduling
    participant CA as Crew App
    
    Note over C,CA: Crew Certification Scenario
    
    C->>EH: Training Completed Event
    Note right of C: Event: TRAINING_COMPLETED<br/>Crew ID: 12345
    
    EH->>SA: Event Processing
    Note right of EH: Immediate Processing<br/>< 100ms latency
    
    par Workflow Initiation
        SA->>SB: Workflow Trigger
        Note right of SA: Certification Process<br/>Multi-step workflow
        
        SA->>SQL: Store Training Record
        Note right of SA: Data Persistence<br/>Audit trail
    end
    
    SB->>LA: Certification Workflow
    Note right of SB: Business Process<br/>Approval required
    
    par Workflow Steps
        LA->>LA: Validate Requirements
        Note right of LA: Check prerequisites<br/>Medical, experience
        
        LA->>LA: Route for Approval
        Note right of LA: Manager approval<br/>Up to 48 hours
        
        LA->>SQL: Update Qualifications
        Note right of LA: Crew certification<br/>Active status
    end
    
    LA->>SR: Certification Complete
    Note right of LA: Real-time Notification<br/>Immediate update
    
    par Consumer Notifications
        SR->>CS: Crew Available
        Note right of SR: Scheduling Update<br/>New qualification
        
        SR->>CA: Certification Alert
        Note right of SR: Mobile Notification<br/>Crew member alert
    end
    
    Note over C,CA: Total Process Time: 2-48 hours<br/>Real-time notifications: < 1 second
```

### Navitaire Inventory Update Flow
```mermaid
graph TB
    subgraph "Inventory Change Detection"
        IC1[Seat Booked<br/>Navitaire Inventory]
        IC2[Price Change<br/>Revenue Management]
        IC3[Class Upgrade<br/>Passenger Service]
    end
    
    subgraph "Real-time Processing"
        EH1[Event Hub<br/>Flight Events]
        SA1[Stream Analytics<br/>Inventory Processing]
    end
    
    subgraph "Dual Output Strategy"
        subgraph "Persistence"
            CD1[Cosmos DB<br/>Flight Inventory]
            RC1[Redis Cache<br/>Current Availability]
        end
        
        subgraph "Real-time Push"
            SR1[SignalR Service<br/>Flight Hub]
            EH2[Event Hub<br/>Distribution]
        end
    end
    
    subgraph "Consumer Updates"
        subgraph "Immediate Updates (< 100ms)"
            WEB1[Booking Website<br/>Seat Map Update]
            MOB1[Mobile App<br/>Availability Alert]
            OPS1[Operations Center<br/>Load Factor]
        end
        
        subgraph "API Updates (< 500ms)"
            PART1[Travel Agencies<br/>Availability Query]
            AIRP1[Airport Systems<br/>Check-in Status]
        end
    end
    
    IC1 --> EH1
    IC2 --> EH1
    IC3 --> EH1
    
    EH1 --> SA1
    
    SA1 --> CD1
    SA1 --> RC1
    SA1 --> SR1
    SA1 --> EH2
    
    SR1 --> WEB1
    SR1 --> MOB1
    SR1 --> OPS1
    
    EH2 --> AF1[Azure Functions<br/>API Processors]
    AF1 --> PART1
    AF1 --> AIRP1
    
    CD1 --> API1[Query APIs]
    RC1 --> API1
    API1 --> PART1
    API1 --> AIRP1
```

### CAE Crew Assignment Flow
```mermaid
graph TB
    subgraph "Crew Assignment Trigger"
        CAT1[Flight Schedule Change<br/>Operations System]
        CAT2[Crew Unavailable<br/>Medical/Personal]
        CAT3[Emergency Replacement<br/>Last Minute]
    end
    
    subgraph "CAE Processing"
        CAE1[Crew Management System<br/>Availability Check]
        CAE2[Qualification Validation<br/>Aircraft Type, Route]
        CAE3[Duty Time Calculation<br/>Regulatory Compliance]
    end
    
    subgraph "Azure Workflow Processing"
        EH1[Event Hub<br/>Crew Events]
        LA1[Logic Apps<br/>Assignment Workflow]
        SQL1[SQL Database<br/>Crew Records]
    end
    
    subgraph "Real-time Distribution"
        SR1[SignalR Service<br/>Crew Hub]
        NH1[Notification Hubs<br/>Mobile Push]
    end
    
    subgraph "Consumer Applications"
        subgraph "Immediate Notifications"
            CREW1[Crew Mobile App<br/>Assignment Alert]
            OPS2[Operations Center<br/>Crew Status]
            SCHED1[Crew Scheduling<br/>Updated Roster]
        end
        
        subgraph "Workflow Updates"
            PAYROLL1[Payroll System<br/>Duty Hours]
            COMP1[Compliance System<br/>Regulatory Check]
        end
    end
    
    CAT1 --> CAE1
    CAT2 --> CAE2
    CAT3 --> CAE3
    
    CAE1 --> EH1
    CAE2 --> EH1
    CAE3 --> EH1
    
    EH1 --> LA1
    LA1 --> SQL1
    
    LA1 --> SR1
    LA1 --> NH1
    
    SR1 --> CREW1
    SR1 --> OPS2
    SR1 --> SCHED1
    
    NH1 --> CREW1
    
    LA1 --> WF1[Workflow Triggers]
    WF1 --> PAYROLL1
    WF1 --> COMP1
```

## Performance and Latency Specifications

### Navitaire Performance Targets
```mermaid
graph TB
    subgraph "Navitaire Event Processing"
        subgraph "High Volume Events"
            HV1[PNR Events<br/>Target: < 100ms<br/>Volume: 30K/hour]
            HV2[Inventory Events<br/>Target: < 50ms<br/>Volume: 15K/hour]
            HV3[Payment Events<br/>Target: < 200ms<br/>Volume: 5K/hour]
        end
        
        subgraph "Processing Capacity"
            PC1[Event Hub<br/>20 TU, Auto-inflate to 100]
            PC2[Stream Analytics<br/>24 SU, Auto-scale]
            PC3[Cosmos DB<br/>10K RU/s, Auto-scale]
        end
        
        subgraph "Consumer Delivery"
            CD1[SignalR WebSocket<br/>< 100ms delivery]
            CD2[API Queries<br/>< 500ms response]
            CD3[Mobile Push<br/>< 2s delivery]
        end
    end
    
    HV1 --> PC1
    HV2 --> PC1
    HV3 --> PC1
    
    PC1 --> PC2
    PC2 --> PC3
    
    PC2 --> CD1
    PC3 --> CD2
    PC2 --> CD3
```

### CAE Performance Targets
```mermaid
graph TB
    subgraph "CAE Event Processing"
        subgraph "Workflow Events"
            WE1[Training Events<br/>Target: < 5min<br/>Volume: 1K/hour]
            WE2[Certification Events<br/>Target: < 2hr<br/>Volume: 200/hour]
            WE3[Assignment Events<br/>Target: < 30s<br/>Volume: 800/hour]
        end
        
        subgraph "Processing Resources"
            PR1[Event Hub<br/>10 TU, Standard tier]
            PR2[Logic Apps<br/>Standard plan]
            PR3[SQL Database<br/>S2 Standard, 50 DTU]
        end
        
        subgraph "Delivery Patterns"
            DP1[Workflow Completion<br/>Minutes to hours]
            DP2[Status Updates<br/>< 1s via SignalR]
            DP3[Mobile Alerts<br/>< 5s push notification]
        end
    end
    
    WE1 --> PR1
    WE2 --> PR1
    WE3 --> PR1
    
    PR1 --> PR2
    PR2 --> PR3
    
    PR2 --> DP1
    PR2 --> DP2
    PR2 --> DP3
```

## Integration Patterns Summary

### Navitaire Integration Pattern
```mermaid
graph LR
    subgraph "Navitaire Source"
        NS1[PSS APIs<br/>REST/GraphQL]
        NS2[Database CDC<br/>SQL Server]
        NS3[Message Queues<br/>IBM MQ/RabbitMQ]
    end
    
    subgraph "Azure Processing"
        AP1[Event Hub<br/>4 partitions]
        AP2[Stream Analytics<br/>Dual output]
        AP3[Cosmos DB + SignalR<br/>Persistence + Real-time]
    end
    
    subgraph "Consumer Delivery"
        CD1[Mobile/Web Apps<br/>WebSocket updates]
        CD2[Partner APIs<br/>REST queries]
        CD3[Operations<br/>Live dashboards]
    end
    
    NS1 --> AP1
    NS2 --> AP1
    NS3 --> AP1
    
    AP1 --> AP2
    AP2 --> AP3
    
    AP3 --> CD1
    AP3 --> CD2
    AP3 --> CD3
```

### CAE Integration Pattern
```mermaid
graph LR
    subgraph "CAE Source"
        CS1[SOAP Services<br/>XML messaging]
        CS2[File Integration<br/>CSV/XML files]
        CS3[Database Direct<br/>Oracle/SQL Server]
    end
    
    subgraph "Azure Processing"
        AP1[Event Hub<br/>2 partitions]
        AP2[Logic Apps<br/>Workflow engine]
        AP3[SQL DB + SignalR<br/>Records + Notifications]
    end
    
    subgraph "Consumer Delivery"
        CD1[Crew Apps<br/>Mobile notifications]
        CD2[Scheduling Systems<br/>Roster updates]
        CD3[Operations<br/>Crew status]
    end
    
    CS1 --> AP1
    CS2 --> AP1
    CS3 --> AP1
    
    AP1 --> AP2
    AP2 --> AP3
    
    AP3 --> CD1
    AP3 --> CD2
    AP3 --> CD3
```
