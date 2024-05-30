UPDATE APP.ENDPOINT
SET ISPATTERNPATH = false,
    BANTIMEINSECONDS = 0,
    IPBASED = false,
    PERMANENTBAN = false,
    REQUESTLIMITCOUNT = 0,
    STATUS = 'NOT_CONFIGURED',
    TIMELIMITINSECONDS = 0
WHERE FQCN = 'org.easypeelsecurity.springdogtest.ExampleController.example3';

UPDATE APP.ENDPOINT
SET ISPATTERNPATH = false,
    BANTIMEINSECONDS = 3600,
    IPBASED = true,
    PERMANENTBAN = false,
    REQUESTLIMITCOUNT = 100,
    STATUS = 'ACTIVE',
    TIMELIMITINSECONDS = 600
WHERE FQCN = 'org.easypeelsecurity.springdogtest.ExampleController.example2';

UPDATE APP.ENDPOINT
SET ISPATTERNPATH = true,
    BANTIMEINSECONDS = 0,
    IPBASED = true,
    PERMANENTBAN = false,
    REQUESTLIMITCOUNT = 0,
    STATUS = 'NOT_CONFIGURED',
    TIMELIMITINSECONDS = 0
WHERE FQCN = 'org.easypeelsecurity.springdogtest.ExampleController.example5';

UPDATE APP.ENDPOINT
SET ISPATTERNPATH = false,
    BANTIMEINSECONDS = 3600,
    IPBASED = true,
    PERMANENTBAN = false,
    REQUESTLIMITCOUNT = 100,
    STATUS = 'ACTIVE',
    TIMELIMITINSECONDS = 600
WHERE FQCN = 'org.easypeelsecurity.springdogtest.ExampleController.example4';

UPDATE APP.ENDPOINT
SET ISPATTERNPATH = false,
    BANTIMEINSECONDS = 3600,
    IPBASED = true,
    PERMANENTBAN = false,
    REQUESTLIMITCOUNT = 100,
    STATUS = 'ACTIVE',
    TIMELIMITINSECONDS = 600
WHERE FQCN = 'org.easypeelsecurity.springdogtest.ExampleController.example';
