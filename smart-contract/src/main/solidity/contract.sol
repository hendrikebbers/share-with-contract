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

    string[] missingIdentifiers;

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
        missingIdentifiers.push(identifier_);
    }

    function removeMissingIdentifierByIndex(uint _index) private {
        require(_index < missingIdentifiers.length, "Method can only be called by consumer");
        for (uint i = _index; i < missingIdentifiers.length - 1; i++) {
            missingIdentifiers[i] = missingIdentifiers[i + 1];
        }
        missingIdentifiers.pop();
    }

    function getIndexOfMissingIdentifier(string memory identifier_) private view returns (uint256) {
        for (uint i = 0; i < missingIdentifiers.length; i++) {
            if (keccak256(abi.encodePacked(missingIdentifiers[i])) == keccak256(abi.encodePacked(identifier_))) {
                return i;
            }
        }
        revert("Identifier not found in missingIdentifiers");
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

        uint256 index = getIndexOfMissingIdentifier(identifier_);
        removeMissingIdentifierByIndex(index);
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

    function getTotalCount() public view returns (uint256){
        require(msg.sender == supplier || msg.sender == consumer, "Method can only be called by consumer or supplier");
        return identifiers.length;
    }

    function getMissingVerificationCount() public view returns (uint256){
        require(msg.sender == supplier || msg.sender == consumer, "Method can only be called by consumer or supplier");
        return missingIdentifiers.length;
    }

    function getMissingVerificationIdentifier(uint256 index) public view returns (string memory){
        require(msg.sender == supplier || msg.sender == consumer, "Method can only be called by consumer or supplier");
        return missingIdentifiers[index];
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