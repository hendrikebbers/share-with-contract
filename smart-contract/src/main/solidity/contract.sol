// SPDX-License-Identifier: GPL-3.0
pragma solidity <0.9.0;

contract SimpleStorage {

    struct Data {
        bytes32 hash;
        uint supplyTimestamp;
        uint verifyTimestamp;
    }

    address supplier;

    address consumer;

    string[] identifiers;

    mapping(string => Data) data;

    constructor (address supplier_, address consumer_) {
        supplier = supplier_;
        consumer = consumer_;
    }

    function addHash(string memory identifier_, bytes32 hash_) public {
        require(msg.sender == supplier, "Method can only be called by supplier");
        require(!exists(identifier_), "Hash for identifier already exists!");

        Data storage d = data[identifier_];
        d.supplyTimestamp = block.timestamp;
        d.hash = hash_;
        identifiers.push(identifier_);
    }

    function verifyHash(string memory identifier_, bytes32 hash_) public {
        require(msg.sender == consumer, "Method can only be called by consumer");
        require(exists(identifier_), "Hash for identifier do not exist!");
        require(!isVerified(identifier_), "Hash for identifier already verified!");

        Data storage d = data[identifier_];
        if (d.hash != hash_) {
            revert("Invalid Hash");
        }
        d.verifyTimestamp = block.timestamp;
    }

    function check(string memory identifier_, bytes32 hash_) public view returns (bool){
        require(msg.sender == supplier || msg.sender == consumer, "Method can only be called by consumer or supplier");
        require(exists(identifier_), "Hash for identifier do not exist!");
        require(isVerified(identifier_), "Hash for identifier not verified!");

        Data storage d = data[identifier_];
        return (d.hash == hash_);
    }

    function getSupplyTimestamp(string memory identifier_) public view returns (uint){
        require(msg.sender == supplier || msg.sender == consumer, "Method can only be called by consumer or supplier");
        require(exists(identifier_), "Hash for identifier do not exist!");

        Data storage d = data[identifier_];
        return d.supplyTimestamp;
    }

    function getVerificationTimestamp(string memory identifier_) public view returns (uint){
        require(msg.sender == supplier || msg.sender == consumer, "Method can only be called by consumer or supplier");
        require(exists(identifier_), "Hash for identifier do not exist!");
        require(isVerified(identifier_), "Hash for identifier not verified!");

        Data storage d = data[identifier_];
        return d.verifyTimestamp;
    }

    function getMissingVerificationCount() public view returns (int){
        require(msg.sender == supplier || msg.sender == consumer, "Method can only be called by consumer or supplier");
        int count = 0;
        for (uint i = 0; i < identifiers.length; i++) {
            string memory identifier = identifiers[i];
            if (!isVerified(identifier)) {
                count++;
            }
        }
        return count;
    }

    function getMissingVerificationIdentifier(int index) public view returns (string memory){
        require(msg.sender == supplier || msg.sender == consumer, "Method can only be called by consumer or supplier");
        int count = 0;
        for (uint i = 0; i < identifiers.length; i++) {
            string memory identifier = identifiers[i];
            if (!isVerified(identifier)) {
                if (count == index) {
                    return identifier;
                }
                count++;
            }
        }
        revert("Invalid index");
    }

    function exists(string memory identifier_) internal view returns (bool){
        Data storage d = data[identifier_];
        if (d.supplyTimestamp != 0) {
            return true;
        }
        return false;
    }

    function isVerified(string memory identifier_) internal view returns (bool){
        Data storage d = data[identifier_];
        if (d.verifyTimestamp != 0) {
            return true;
        }
        return false;
    }
}